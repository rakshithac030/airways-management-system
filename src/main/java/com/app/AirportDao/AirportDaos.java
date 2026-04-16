package com.app.AirportDao;

import java.util.List;

import com.app.entity.AirportEntity;

public interface AirportDaos {
	
	boolean saveAirportEntity(AirportEntity entity);
	  AirportEntity getAirportEntityByID(Long id);
	  AirportEntity getAirportEntityByName(String airportName,String airportLocation);
	  List<AirportEntity> getAllAirport();
	  boolean updateAirportEntity(AirportEntity entity);
	  boolean deleteAirportById(Long id);
	  long countAirports();
}
