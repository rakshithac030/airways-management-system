package com.app.controller;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.app.AirportDao.FlightDao;
import com.app.entity.BookingEntity;
import com.app.entity.UserEntity;
import com.app.enums.FlightStatus;
import com.app.services.BookingValidation;
import com.app.services.FlightValidationService;

@Controller
public class DashboardController  {
	
    @Autowired private FlightValidationService flightService;
    @Autowired private BookingValidation bookingService;

    @RequestMapping(value = { "/","/dashboard"} ,method = RequestMethod.GET)
    public String home(HttpSession session,Model model) {

    	model.addAttribute("activeFlights",
                flightService.getActiveFlightCount());

        model.addAttribute("delayedFlights",
                flightService.getDelayedFlightCount());

        model.addAttribute("cancelledFlights",
                flightService.getCancelledFlightCount());

        model.addAttribute("recentFlights",
                flightService.getRecentProblemFlights(5));

        UserEntity user = (UserEntity) session.getAttribute("loggedInUser");

        if (user != null) {

        	Long userId = user.getUserId();
            long myTrips = bookingService.countByUserId(userId);

            BookingEntity upcoming =
                    bookingService.getUpcomingBooking(user.getUserId());

            model.addAttribute("myBookingsCount", myTrips);
            model.addAttribute("upcomingBooking", upcoming);
        }

        return "dashboard"; 
    }
    
}
