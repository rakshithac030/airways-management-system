package com.app.entity;

import java.time.LocalDateTime;
import javax.persistence.*;

import com.app.enums.FlightStatus;

@Entity
@Table(
    name = "flight",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"flight_code", "departure_time"})
    }
)
public class FlightEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flight_id")
    private Long flightId;

    @Column(name = "flight_code", nullable = false)
    private String flightCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_airport_id", nullable = false)
    private AirportEntity sourceAirport;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_airport_id", nullable = false)
    private AirportEntity destinationAirport;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(name = "total_seats", nullable = false)
    private int totalSeats;

    @Column(name = "available_seats", nullable = false)
    private int availableSeats;

    @Column(name = "base_fare", nullable = false)
    private double baseFare;

    @Column(name = "emergency_fare", nullable = false)
    private double emergencyFare;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FlightStatus status = FlightStatus.SCHEDULED;

    public FlightEntity() {}

    @Transient
    public LocalDateTime getArrivalTime() {
        return departureTime.plusMinutes(durationMinutes);
    }
    
    @Transient
    public boolean isDeparted() {
        return departureTime.isBefore(LocalDateTime.now());
    }
    
    @Transient
    public boolean isFutureFlight() {
        return departureTime.isAfter(LocalDateTime.now());
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public void setFlightCode(String flightCode) {
        this.flightCode = flightCode;
    }

    public AirportEntity getSourceAirport() {
        return sourceAirport;
    }

    public void setSourceAirport(AirportEntity sourceAirport) {
        this.sourceAirport = sourceAirport;
    }

    public AirportEntity getDestinationAirport() {
        return destinationAirport;
    }

    public void setDestinationAirport(AirportEntity destinationAirport) {
        this.destinationAirport = destinationAirport;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public double getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(double baseFare) {
        this.baseFare = baseFare;
    }

    public double getEmergencyFare() {
        return emergencyFare;
    }

    public void setEmergencyFare(double emergencyFare) {
        this.emergencyFare = emergencyFare;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "FlightEntity{" +
                "id=" + flightId +
                ", code='" + flightCode + '\'' +
                ", from=" + (sourceAirport != null ? sourceAirport.getIataCode() : null) +
                ", to=" + (destinationAirport != null ? destinationAirport.getIataCode() : null) +
                ", departure=" + departureTime +
                ", duration=" + durationMinutes + " mins" +
                ", seats=" + availableSeats + "/" + totalSeats +
                ", fare=" + baseFare +
                ", status=" + status +
                '}';
    }
}
