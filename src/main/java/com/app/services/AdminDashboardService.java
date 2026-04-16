package com.app.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.AirportDao.AirportDaos;
import com.app.AirportDao.BookingDao;
import com.app.AirportDao.FlightDao;
import com.app.AirportDao.UserDao;
import com.app.entity.FlightEntity;
import com.app.enums.BookingStatus;
import com.app.enums.FlightStatus;

@Service
public class AdminDashboardService {

    @Autowired private UserDao userDao;
    @Autowired private AirportDaos airportDao;
    @Autowired private FlightDao flightDao;
    @Autowired private BookingDao bookingDao;

    public long totalUsers() {
        return userDao.countUsers();
    }

    public long totalAirports() {
        return airportDao.countAirports();
    }

    public long totalFlights() {
        return flightDao.countFlights();
    }

    public long totalBookings() {
        return bookingDao.countBookings();
    }

    public long confirmedBookings() {
        return bookingDao.countByStatus(BookingStatus.CONFIRMED);
    }

    public long pendingBookings() {
        return bookingDao.countByStatus(BookingStatus.PENDING);
    }

    public long cancelledBookings() {
        return bookingDao.countByStatus(BookingStatus.CANCELLED);
    }

    public long seatsSoldToday() {
        return bookingDao.seatsSoldToday();
    }

    public long flightsDepartingToday() {
        return flightDao.flightsDepartingToday();
    }
    public List<FlightEntity> getRecentActivityFlights() {
        return flightDao.getRecentProblemFlights(5);
    }

    public long countActive() {
        return flightDao.countByStatus(FlightStatus.SCHEDULED);
    }

    public long countDelayed() {
        return flightDao.countByStatus(FlightStatus.DELAYED);
    }

    public long countCancelled() {
        return flightDao.countByStatus(FlightStatus.CANCELLED);
    }
}
