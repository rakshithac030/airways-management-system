package com.app.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.app.AirportDao.FlightDao;
import com.app.dto.ConnectingFlightDTO;
import com.app.entity.AirportEntity;
import com.app.entity.FlightEntity;
import com.app.enums.FlightStatus;
import com.app.services.AirportService;
@Component
public class FlightValidationService {

    private final FlightDao flightDao;
    private final AirportService airportService;

    @Autowired
    public FlightValidationService(FlightDao flightDao, AirportService airportService) {
        this.flightDao = flightDao;
        this.airportService = airportService;
        System.out.println("✅ FlightValidation initialized");
    }

    public boolean validateAndSaveFlight(
            String flightCode,
            Long sourceAirportId,
            Long destinationAirportId,
            LocalDateTime departureTime
    ) {
        System.out.println("✈️ validateAndSaveFlight invoked for flightCode=" + flightCode);

        if (flightCode == null || flightCode.isBlank()) {
            System.out.println("Invalid flight code");
            return false;
        }

        if (sourceAirportId == null || destinationAirportId == null) {
            System.out.println(" Airport IDs cannot be null");
            return false;
        }

        if (sourceAirportId.equals(destinationAirportId)) {
            System.out.println(" Source and destination airports cannot be same");
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        if (departureTime == null || !departureTime.isAfter(now)) {
            System.out.println("Invalid departure time: " + departureTime + " < " + now);
            return false;
        }


        AirportEntity sourceAirport =
        		airportService.getAirportById(sourceAirportId);
        AirportEntity destinationAirport =
        		airportService.getAirportById(destinationAirportId);

        if (sourceAirport == null || destinationAirport == null) {
            System.out.println("Invalid airport selection");
            return false;
        }


        FlightEntity flight = new FlightEntity();
        flight.setFlightCode(flightCode);
        flight.setSourceAirport(sourceAirport);
        flight.setDestinationAirport(destinationAirport);
        flight.setDepartureTime(departureTime);
        flight.setStatus(FlightStatus.SCHEDULED);
        flight.setTotalSeats(180);        
        flight.setAvailableSeats(180);
        flight.setBaseFare(4500.0);
        flight.setEmergencyFare(6500.0); 
        flight.setDurationMinutes(150); 

        boolean saved = flightDao.saveFlight(flight);
        System.out.println(saved
                ? "✅ Flight created successfully"
                : "❌ Failed to save flight");

        return saved;
    }

    public List<FlightEntity> searchFlights(
            Long sourceId,
            Long destinationId,
            LocalDate travelDate
    ) {
        List<FlightEntity> flights =
            flightDao.getFlightsBySourceAndDestination(sourceId, destinationId);

        return flights.stream()
                .filter(f -> f.getStatus() == FlightStatus.SCHEDULED)
                .filter(f -> {
                    if (travelDate != null) {
                        return f.getDepartureTime().toLocalDate().equals(travelDate);
                    }
                    return f.getDepartureTime().isAfter(LocalDateTime.now());
                })
                .toList();

    }

    public List<FlightEntity> getAllFlights() {
        System.out.println("Fetching all flights");
        return flightDao.getAllFlights();
    }
    
    public FlightEntity getFlightById(Long flightId) {

        System.out.println(" getFlightById invoked with ID=" + flightId);

        if (flightId == null || flightId <= 0) {
            System.out.println(" Invalid flight ID");
            return null;
        }

        return flightDao.getFlightById(flightId);
    }

    public boolean updateFlight(
            Long flightId,
            Long sourceAirportId,
            Long destinationAirportId,
            LocalDateTime departureTime,
            FlightStatus status
    ) {
        System.out.println(" updateFlight invoked for ID=" + flightId);

        FlightEntity flight = flightDao.getFlightById(flightId);
        if (flight == null) {
            System.out.println(" Flight not found");
            return false;
        }
        
        if (sourceAirportId != null && destinationAirportId != null) {

            if (sourceAirportId.equals(destinationAirportId)) {
                System.out.println("Source & destination cannot be same");
                return false;
            }

            AirportEntity source =
            		airportService.getAirportById(sourceAirportId);
            AirportEntity dest =
            		airportService.getAirportById(destinationAirportId);

            if (source == null || dest == null) {
                System.out.println(" Invalid airport update");
                return false;
            }

            flight.setSourceAirport(source);
            flight.setDestinationAirport(dest);
        }

        if (departureTime != null && departureTime.isAfter(LocalDateTime.now())) {
            flight.setDepartureTime(departureTime);
        }
        if (status == FlightStatus.DELAYED || status == FlightStatus.CANCELLED) {
            flight.setStatus(status);
        }


        return flightDao.updateFlight(flight);
    }
    
    public List<ConnectingFlightDTO> findConnectingFlights(
            Long sourceId,
            Long destinationId
    ) {
        List<FlightEntity> fromSource = flightDao.getFlightsFromSource(sourceId);
        List<FlightEntity> toDestination = flightDao.getFlightsToDestination(destinationId);

        List<ConnectingFlightDTO> connections = new ArrayList<>();

        for (FlightEntity first : fromSource) {
            for (FlightEntity second : toDestination) {

                if (first.getDestinationAirport().getAirportId()
                        .equals(second.getSourceAirport().getAirportId())) {

                	 connections.add(new ConnectingFlightDTO(first, second));
                }
            }
        }

        return connections;
    }

    

    public boolean hardDeleteFlight(Long flightId) {
        System.out.println(" hardDeleteFlight invoked for ID=" + flightId);
        FlightEntity flight = flightDao.getFlightById(flightId);
        if (flight == null) {
            throw new IllegalStateException("Flight not found");
        }
        validateFlightNotDeparted(flight); 

        return flightDao.hardDeleteFlight(flightId);
    }

    public boolean cancelFlight(Long flightId) {

        System.out.println(" Cancelling flight ID=" + flightId);

        if (flightId == null || flightId <= 0) {
            System.out.println(" Invalid flight ID");
            return false;
        }

        FlightEntity flight = flightDao.getFlightById(flightId);

        if (flight == null) {
            System.out.println(" Flight not found");
            return false;
        }

        if (flight.getStatus() == FlightStatus.CANCELLED) {
            System.out.println("Flight already cancelled");
            return false;
        }
        validateFlightNotDeparted(flight);

        // 3️⃣ apply cancellation
        flight.setStatus(FlightStatus.CANCELLED);
        flight.setAvailableSeats(0);

        return flightDao.updateFlight(flight);
    }
    
    public List<FlightEntity> getUpcomingFlightsPage(int page, int size) {

        List<FlightEntity> flights =flightDao.getUpcomingFlightsPage(page, size);
        return flights;
    }

    
    public List<FlightEntity> getAllFlightsPage(int page, int size) {
        return flightDao.getAllFlightsPage(page, size);
    }
    
    public long getActiveFlightCount() {
        return flightDao.countByStatus(FlightStatus.SCHEDULED);
    }

    public long getDelayedFlightCount() {
        return flightDao.countByStatus(FlightStatus.DELAYED);
    }

    public long getCancelledFlightCount() {
        return flightDao.countByStatus(FlightStatus.CANCELLED);
    }
    
    public List<FlightEntity> getRecentProblemFlights(int limit) {
        return flightDao.getRecentProblemFlights(limit);
    }
    
    public List<FlightEntity> findByStatus(FlightStatus status) {
        return flightDao.findByStatus(status);
    }
    
    public void validateFlightNotDeparted(FlightEntity flight) {
        if (flight.getDepartureTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException(
                "Operation not allowed. Flight already departed."
            );
        }
    }

}
