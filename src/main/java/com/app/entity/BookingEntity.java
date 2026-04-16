package com.app.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.*;

import com.app.enums.BookingStatus;

@Entity
@Table(name = "booking")
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private FlightEntity flight;

    @Column(name = "seats_booked", nullable = false)
    private int seatsBooked;
    
    @Column(name = "applied_fare", nullable = false)
    private double appliedFare;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "booking_time", nullable = false)
    private LocalDateTime bookingTime = LocalDateTime.now();
    
    @Column(name = "travel_date")
    private LocalDate travelDate;
    
    @Transient
    public boolean isCancellable() {
        if (flight == null || flight.getDepartureTime() == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(
            flight.getDepartureTime().minusHours(2)
        );
    }

    
    
    public BookingEntity() {}
    
    public BookingEntity(UserEntity user, FlightEntity flight, int seats) {
        this.user = user;
        this.flight = flight;
        this.seatsBooked = seats;
        this.travelDate = flight.getDepartureTime().toLocalDate();
    }

    public Long getBookingId() {
        return bookingId;
    }

    public UserEntity getUser() {
        return user;
    }

    public FlightEntity getFlight() {
        return flight;
    }

    public int getSeatsBooked() {
        return seatsBooked;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setFlight(FlightEntity flight) {
        this.flight = flight;
    }

    public void setSeatsBooked(int seatsBooked) {
        this.seatsBooked = seatsBooked;
    }
    
    public double getAppliedFare() {
        return appliedFare;
    }

    public void setAppliedFare(double appliedFare) {
        this.appliedFare = appliedFare;
    }


    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }
    public LocalDate getTravelDate() {
        return travelDate;
    }
    public void setTravelDate(LocalDate travelDate) {
        this.travelDate = travelDate;
    }
    
    @Override
    public String toString() {
        return "BookingEntity{" +
                "id=" + bookingId +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ", flightId=" + (flight != null ? flight.getFlightId() : null) +
                ", seats=" + seatsBooked +
                ", fare=" + appliedFare +
                ", status=" + status +
                ", travelDate=" + travelDate +
                '}';
    }
}
