package com.app.AirportDao;

import java.util.List;
import com.app.entity.RunwayEntity;

public interface RunwayDao {

    boolean saveRunway(RunwayEntity runway);

    RunwayEntity getRunwayById(Long runwayId);

    boolean updateRunway(RunwayEntity runway);

	List<RunwayEntity> getRunwaysByAirportId(Long airportId);

	boolean deleteRunway(Long runwayId);

}
