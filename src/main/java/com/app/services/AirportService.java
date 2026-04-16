package com.app.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.app.AirportDao.AirportDaos;
import com.app.entity.AirportEntity;
import com.app.enums.AirportStatus;

@Component
public class AirportService {

    private final AirportDaos airportDaos;

    @Autowired
    public AirportService(AirportDaos airportDaos) {
        this.airportDaos = airportDaos;
    }
    
    public boolean validateAndSaveAirport(
            String name,
            String location,
            String iataCode,
            int parkingCapacity,
            boolean international
    ) {

        if (name == null || name.isBlank()) return false;
        if (location == null || location.isBlank()) return false;
        if (iataCode == null || iataCode.length() != 3) return false;
        if (parkingCapacity < 0) return false;

        AirportEntity airport = new AirportEntity();
        airport.setAirportName(name);
        airport.setAirportLocation(location);
        airport.setIataCode(iataCode);
        airport.setParkingCapacity(parkingCapacity);
        airport.setInternational(international);

        airport.setAirportCode("AIR-" + System.currentTimeMillis());
        return airportDaos.saveAirportEntity(airport);
    }
    
    public boolean validAirportId(Long airportId) {
        return airportId != null && airportId > 0;
    }
    
    public AirportEntity getAirportById(Long airportId) {
        if (!validAirportId(airportId)) return null;
        return airportDaos.getAirportEntityByID(airportId);
    }
    
    public AirportEntity getAirportByName(String name, String location) {
        if (name == null || name.isBlank()) return null;
        if (location == null || location.isBlank()) return null;

        return airportDaos.getAirportEntityByName(name, location);
    }
    
    public List<AirportEntity> getAllAirports() {
        return airportDaos.getAllAirport();
    }
    
    public boolean updateAirport(
            Long airportId,
            String name,
            String location,
            int parkingCapacity
    ) {

        AirportEntity existing = airportDaos.getAirportEntityByID(airportId);
        if (existing == null) return false;

        if (name != null && !name.isBlank()) {
            existing.setAirportName(name);
        }

        if (location != null && !location.isBlank()) {
            existing.setAirportLocation(location);
        }

        if (parkingCapacity >= 0) {
            existing.setParkingCapacity(parkingCapacity);
        }

        return airportDaos.updateAirportEntity(existing);
    }
    
    public boolean deleteAirport(Long airportId) {
        AirportEntity existing = airportDaos.getAirportEntityByID(airportId);
        if (existing == null) return false;

        return airportDaos.deleteAirportById(airportId);
    }
    
    public boolean updateAirportStatus(Long airportId, AirportStatus status) {
        AirportEntity airport = airportDaos.getAirportEntityByID(airportId);
        if (airport == null || status == null) return false;

        airport.setStatus(status);
        return airportDaos.updateAirportEntity(airport);
    }

}
