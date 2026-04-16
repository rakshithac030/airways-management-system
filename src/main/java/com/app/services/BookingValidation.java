package com.app.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.AirportDao.BookingDao;
import com.app.AirportDao.FlightDao;
import com.app.entity.BookingEntity;
import com.app.entity.FlightEntity;
import com.app.entity.UserEntity;
import com.app.enums.BookingStatus;
import com.app.enums.FlightStatus;

@Service
public class BookingValidation {

	private final BookingDao bookingDao;
    private final FlightDao flightDao;
    private static final int MAX_SEATS_PER_BOOKING = 10;
    
    @Autowired
    public BookingValidation(BookingDao bookingDao, FlightDao flightDao) {
        this.bookingDao = bookingDao;
        this.flightDao = flightDao;
    }

    public boolean createBooking(UserEntity user, Long flightId, int seats) {

        validateParameters(user, flightId, seats);
        
        FlightEntity flight = flightDao.getFlightById(flightId);
        if (flight == null) {
            throw new IllegalArgumentException("Flight not found");
        }
        validateFlightStatus(flight);
        
        validateSeatAvailability(flight, seats);
        
        validateDepartureTime(flight);
        
        // 6. Calculate fare
        double totalFare = calculateFare(flight, seats);
        
        // 7. Create booking
        BookingEntity booking = createBookingEntity(user, flight, seats, totalFare);
        
        return bookingDao.saveBooking(booking);
    }
    
    private void validateParameters(UserEntity user, Long flightId, int seats) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (flightId == null || flightId <= 0) {
            throw new IllegalArgumentException("Invalid flight ID");
        }
        
        if (seats <= 0) {
            throw new IllegalArgumentException("Number of seats must be greater than zero");
        }
        
        if (seats > MAX_SEATS_PER_BOOKING) {
            throw new IllegalArgumentException(
                String.format("Maximum %d seats allowed per booking", MAX_SEATS_PER_BOOKING));
        }
    }
    
    private void validateFlightStatus(FlightEntity flight) {
        if (flight.getStatus() == FlightStatus.CANCELLED) {
            throw new IllegalArgumentException("Flight is cancelled");
        }
        
        if (flight.getStatus() == FlightStatus.COMPLETED) {
            throw new IllegalArgumentException("Flight has already completed");
        }
    }
    
    private void validateSeatAvailability(FlightEntity flight, int seats) {
        if (seats > flight.getAvailableSeats()) {
            throw new IllegalArgumentException(
                String.format("Only %d seats available", flight.getAvailableSeats()));
        }
    }
    
    private void validateDepartureTime(FlightEntity flight) {
        LocalDateTime now = LocalDateTime.now();
        if (flight.getDepartureTime().isBefore(now)) {
            throw new IllegalArgumentException("Flight has already departed");
        }
    }
    
    private double calculateFare(FlightEntity flight, int seats) {
        LocalDateTime now = LocalDateTime.now();
        long hoursToDeparture = java.time.Duration.between(now, flight.getDepartureTime()).toHours();
        double farePerSeat = (hoursToDeparture < 6) ? flight.getEmergencyFare() : flight.getBaseFare();
        return farePerSeat * seats;
    }
    
    private BookingEntity createBookingEntity(UserEntity user, FlightEntity flight, 
                                             int seats, double totalFare) {
        BookingEntity booking = new BookingEntity();
        booking.setUser(user);
        booking.setFlight(flight);
        booking.setSeatsBooked(seats);
        booking.setAppliedFare(totalFare);
        booking.setStatus(BookingStatus.PENDING);
        booking.setTravelDate(flight.getDepartureTime().toLocalDate());
        return booking;
    }


    public boolean confirmBooking(Long bookingId, UserEntity user) {

	    if (bookingId == null || user == null) return false;

	    BookingEntity booking = bookingDao.getBookingById(bookingId);
	    if (booking == null) return false;

	    if (booking.getUser().getUserId() != user.getUserId())
	        return false;

	    if (booking.getStatus() != BookingStatus.PENDING)
	        return false;

	    return bookingDao.confirmBookingAndDeductSeats(bookingId);
	}


 	public boolean cancelBooking(Long bookingId, UserEntity user) {

	    if (bookingId == null || user == null) return false;

	    BookingEntity booking = bookingDao.getBookingById(bookingId);
	    if (booking == null) return false;

	    if (booking.getUser().getUserId() != user.getUserId())
	        return false;

	    if (booking.getStatus() == BookingStatus.CANCELLED)
	        return false;
	    
	    LocalDateTime now = LocalDateTime.now();
	    LocalDateTime departure = booking.getFlight().getDepartureTime();

	    if (now.isAfter(departure.minusHours(2))) {
	        throw new IllegalStateException(
	            "Cancellation not allowed within 2 hours of departure"
	        );
	    }


	    return bookingDao.cancelBookingAndRestoreSeats(bookingId);
	}


 	public boolean adminCancelBooking(Long bookingId) {

	    if (bookingId == null) return false;

	    BookingEntity booking = bookingDao.getBookingById(bookingId);
	    if (booking == null) return false;

	    if (booking.getStatus() == BookingStatus.CANCELLED) return false;

	    if (booking.getStatus() == BookingStatus.CONFIRMED) {

	        FlightEntity flight = booking.getFlight();

	        flight.setAvailableSeats(
	            flight.getAvailableSeats() + booking.getSeatsBooked()
	        );

	        flightDao.updateFlight(flight);
	    }

	    booking.setStatus(BookingStatus.CANCELLED);
	    return bookingDao.updateBooking(booking);
	}


    public List<BookingEntity> getBookingsByUser(Long userId) {
        return bookingDao.getBookingsByUser(userId);
    }
    
    
    public List<BookingEntity> getAllBookings() {
        return bookingDao.getAllBookings();
    }

    public boolean updateBookingStatus(Long bookingId, BookingStatus status) {

        if (bookingId == null || status == null) return false;
        if (status == BookingStatus.CANCELLED) return false; // force cancel handled separately

        BookingEntity booking = bookingDao.getBookingById(bookingId);
        if (booking == null) return false;

        booking.setStatus(status);
        return bookingDao.updateBooking(booking);
    }
    
    public long countByUserId(Long userId) {
        return bookingDao.countByUserId(userId);
    }
    
    public BookingEntity getUpcomingBooking(Long userId) {
        return bookingDao.findUpcomingBookingByUserId(userId);
    }
    
    public double calculateBookingFare(Long flightId, int seats) {
        FlightEntity flight = flightDao.getFlightById(flightId);
        if (flight == null) throw new IllegalArgumentException("Flight not found");
        
        LocalDateTime now = LocalDateTime.now();
        long hoursToDeparture = 
            java.time.Duration.between(now, flight.getDepartureTime()).toHours();
        
        double farePerSeat;
        if (hoursToDeparture < 6) {
            farePerSeat = flight.getEmergencyFare();
        } else {
            farePerSeat = flight.getBaseFare();
        }
        
        return farePerSeat * seats;
    }



}
