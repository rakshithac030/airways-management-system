package com.app.AirportDao;

import java.time.LocalDate;
import java.util.List;
import com.app.entity.FlightEntity;
import com.app.enums.FlightStatus;

public interface FlightDao {

    boolean saveFlight(FlightEntity flight);
    FlightEntity getFlightById(Long flightId);
    List<FlightEntity> getAllFlights();
    List<FlightEntity> getFlightsBySourceAndDestination(
            Long sourceAirportId,
            Long destinationAirportId
    );
    List<FlightEntity> getFlightsFromSource(Long sourceAirportId);
    List<FlightEntity> getFlightsToDestination(Long destinationAirportId);
    boolean updateFlight(FlightEntity flight);
    boolean hardDeleteFlight(Long flightId);
	long countFlights();
	long flightsDepartingToday();
	List<FlightEntity> getUpcomingFlightsPage(int page, int size);

	List<FlightEntity> getAllFlightsPage(int page, int size);
	List<FlightEntity> getRecentProblemFlights(int limit);
	long countByStatus(FlightStatus status);
	List<FlightEntity> findByStatus(FlightStatus status);



}
