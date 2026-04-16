package com.app.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.app.AirportDao.RunwayDao;
import com.app.entity.AirportEntity;
import com.app.entity.RunwayEntity;

@Component
public class RunwayValidation {

    private final RunwayDao runwayDao;
    private final AirportService airportValidation;

    @Autowired
    public RunwayValidation(RunwayDao runwayDao, AirportService airportValidation) {
        this.runwayDao = runwayDao;
        this.airportValidation = airportValidation;
    }

    public boolean validateAndSaveRunway(String runwayNumber, double length, String surfaceType, Long airportId) {
        System.out.println("validateAndSaveRunway invoked for runway: " + runwayNumber);

        AirportEntity airport = airportValidation.getAirportById(airportId);
        if (airport == null) {
            System.out.println("Invalid airport ID: " + airportId);
            return false;
        }
        List<RunwayEntity> existingRunways =
                runwayDao.getRunwaysByAirportId(airportId);

        for (RunwayEntity r : existingRunways) {
            if (r.getRunwayNumber().equalsIgnoreCase(runwayNumber)) {
                System.out.println("Duplicate runway number for this airport");
                return false;
            }
        }


        boolean valid = true;
        if (runwayNumber == null || runwayNumber.isBlank()) { valid = false; System.out.println("Invalid runway number"); }
        if (length <= 0) { valid = false; System.out.println("Invalid runway length"); }
        if (surfaceType == null || surfaceType.isBlank()) { valid = false; System.out.println("Invalid surface type"); }

        if (valid) {
            RunwayEntity runway = new RunwayEntity(runwayNumber, length, surfaceType, airport);
            boolean saved = runwayDao.saveRunway(runway);
            if (saved) {
                System.out.println("Runway saved successfully for airport: " + airport.getAirportName());
            }
            return saved;
        }

        System.out.println("Runway validation failed for runway: " + runwayNumber);
        return false;
    }

    public boolean validRunwayId(Long runwayId) {
        System.out.println("validRunwayId check for ID: " + runwayId);
        return runwayId > 0;
    }

    public RunwayEntity getRunwayById(Long runwayId) {
        if (!validRunwayId(runwayId)) return null;
        System.out.println("Fetching runway with ID: " + runwayId);
        return runwayDao.getRunwayById(runwayId);
    }
    public List<RunwayEntity> getRunwaysByAirportId(Long airportId) {
        System.out.println("Fetching all runways for airport ID: " + airportId);
        if (airportId <= 0) return null;
        return runwayDao.getRunwaysByAirportId(airportId);
    }

    public boolean updateRunway(
            Long runwayId,
            String runwayNumber,
            double length,
            String surfaceType,
            Long airportId) {

        System.out.println("updateRunway invoked for runway ID: " + runwayId);

        RunwayEntity existing = runwayDao.getRunwayById(runwayId);
        if (existing == null) {
            System.out.println("Runway not found with ID: " + runwayId);
            return false;
        }

        if (runwayNumber != null && !runwayNumber.isBlank()) {
            existing.setRunwayNumber(runwayNumber);
        }

        if (length > 0) {
            existing.setLength(length);
        }

        if (surfaceType != null && !surfaceType.isBlank()) {
            existing.setSurfaceType(surfaceType);
        }

        return runwayDao.updateRunway(existing);
    }


    public boolean deleteRunway(Long runwayId) {
        System.out.println("deleteRunway invoked for runway ID: " + runwayId);

        RunwayEntity existing = runwayDao.getRunwayById(runwayId);
        if (existing == null) {
            System.out.println("Runway not found: " + runwayId);
            return false;
        }

        boolean deleted = runwayDao.deleteRunway(runwayId);

        if (deleted) {
            System.out.println("Runway deleted successfully: " + runwayId);
        }

        return deleted;
    }

}
