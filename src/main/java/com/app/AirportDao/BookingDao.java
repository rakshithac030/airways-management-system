package com.app.AirportDao;

import java.time.LocalDate;
import java.util.List;
import com.app.entity.BookingEntity;
import com.app.enums.BookingStatus;

public interface BookingDao {

    boolean saveBooking(BookingEntity booking);

    BookingEntity getBookingById(Long bookingId);

    List<BookingEntity> getBookingsByUser(Long userId);

    List<BookingEntity> getAllBookings();

    boolean cancelBookingAndRestoreSeats(Long bookingId);
    
    long countBookings();
    long countByStatus(BookingStatus status);
    long seatsSoldToday();
    long countByUserId(Long userId);
    boolean updateBooking(BookingEntity booking);
    BookingEntity findUpcomingBookingByUserId(Long userId);
	boolean confirmBookingAndDeductSeats(Long bookingId);
	int sumSeatsByFlightAndDate(Long flightId, LocalDate travelDate);


}

