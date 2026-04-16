package com.app.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import static com.app.util.AuthUtil.*;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.app.entity.FlightEntity;
import com.app.enums.FlightStatus;
import com.app.services.FlightValidationService;
import com.app.services.AirportService;

@Controller
@RequestMapping("/flights")
public class FlightController {

    private final FlightValidationService flightValidation;
    private final AirportService airportValidation;

    @Autowired
    public FlightController(
            FlightValidationService flightValidation,
            AirportService airportValidation
    ) {
        this.flightValidation = flightValidation;
        this.airportValidation = airportValidation;
        System.out.println("✅ FlightController initialized");
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String flightsHome() {
    	return "redirect:/flights/view";
    }

    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public String listFlights(
            @RequestParam(required = false) FlightStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String mode,
            HttpSession session,
            Model model) {

        int pageSize = 5;
        String role = (String) session.getAttribute("role");

        boolean liveMode = "LIVE".equalsIgnoreCase(mode);
        List<FlightEntity> flights;

        if (liveMode) {
            flights = flightValidation.getRecentProblemFlights(10);
        }
        else if (status != null) {
            flights = flightValidation.findByStatus(status);
        } else if ("ADMIN".equals(role)) {
            flights = flightValidation.getAllFlightsPage(page, pageSize);
        } else {
            flights = flightValidation.getUpcomingFlightsPage(page, pageSize);
        }

        model.addAttribute("flights", flights);

        model.addAttribute("mode", liveMode ? "LIVE" : "BROWSE");
        model.addAttribute("airports", airportValidation.getAllAirports());
        model.addAttribute("flightStatuses", FlightStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("page", page);
        model.addAttribute("now", LocalDateTime.now());

        return "flights";
    }
    
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String searchFlights(
            @RequestParam Long sourceAirportId,
            @RequestParam Long destinationAirportId,
            @RequestParam(required = false) LocalDate travelDate,
            HttpSession session,
            Model model
    ) {
        model.addAttribute("airports", airportValidation.getAllAirports());
        model.addAttribute("flightStatuses", FlightStatus.values());
        model.addAttribute("mode", "BROWSE");
        model.addAttribute("page", 0);
        model.addAttribute("now", LocalDateTime.now());
        if (sourceAirportId.equals(destinationAirportId)) {
            model.addAttribute("responseMessage",
                    "Source and destination cannot be the same");
            model.addAttribute("flights", List.of());
            return "flights";
        }
        List<FlightEntity> directFlights =
                flightValidation.searchFlights(
                        sourceAirportId, destinationAirportId, travelDate
                );
        model.addAttribute("flights", directFlights);
        
        if (directFlights.isEmpty()) {
            model.addAttribute("noDirectFlights", true);
            model.addAttribute(
                "connectingFlights",
                flightValidation.findConnectingFlights(
                        sourceAirportId, destinationAirportId
                )
            );
        }

        return "flights";
    }

	 @RequestMapping(value = "/create", method = RequestMethod.POST)
	 public String createFlight(
	         @RequestParam String flightCode,
	         @RequestParam Long sourceAirportId,
	         @RequestParam Long destinationAirportId,
	         @RequestParam String departureTime,
	         HttpSession session,
	         RedirectAttributes redirectAttributes
	 ) {
	     if (!isAdmin(session)) {
	         redirectAttributes.addFlashAttribute("responseMessage", "Admin access required");
	         return "redirect:/flights/view";
	     }
	
	     DateTimeFormatter formatter =
	             DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
	
	     LocalDateTime time;
	     try {
	         time = LocalDateTime.parse(departureTime, formatter);
	     } catch (Exception e) {
	         redirectAttributes.addFlashAttribute(
	                 "responseMessage",
	                 "Invalid departure time format"
	         );
	         return "redirect:/flights/view";
	     }
	
	     boolean saved = flightValidation.validateAndSaveFlight(
	             flightCode, sourceAirportId, destinationAirportId, time
	     );
	
	     redirectAttributes.addFlashAttribute(
	             "responseMessage",
	             saved ? "Flight created successfully" : "Flight creation failed"
	     );
	
	     return "redirect:/flights/view";
	 }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String updateFlight(
            @RequestParam Long flightId,
            @RequestParam(required = false) Long sourceAirportId,
            @RequestParam(required = false) Long destinationAirportId,
            @RequestParam String departureTime,
            @RequestParam FlightStatus status,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        System.out.println("🔄 updateFlight()");

        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("responseMessage", "Admin access required");
            return "redirect:/flights/view";
        }

        try 
        {
	        boolean updated = flightValidation.updateFlight(
	                flightId,
	                sourceAirportId,
	                destinationAirportId,
	                LocalDateTime.parse(departureTime),
	                status
	        );
	        if (updated) {
	            redirectAttributes.addFlashAttribute("responseMessage", "Flight updated");
	            redirectAttributes.addFlashAttribute("updatedFlightId", flightId);
	        } else {
	            redirectAttributes.addFlashAttribute("responseMessage", "Update failed");
	        }
	    } catch (IllegalStateException e) {
	        // 🚫 Flight already departed (business rule)
	        redirectAttributes.addFlashAttribute("responseMessage", e.getMessage());
	    }


        return "redirect:/flights/view?page=0";
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public String cancelFlight(
            @RequestParam Long flightId,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        System.out.println("🚫 cancelFlight()");

        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("responseMessage", "Admin access required");
            return "redirect:/flights/view";
        }
        try{
        	boolean cancelled = flightValidation.cancelFlight(flightId);
        	redirectAttributes.addFlashAttribute("responseMessage",cancelled ? "Flight cancelled" : "Cancel failed");
	    } catch (IllegalStateException e) {
	        redirectAttributes.addFlashAttribute("responseMessage", e.getMessage());
	    }

        return "redirect:/flights/view";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteFlight(
            @RequestParam Long flightId,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        System.out.println("🔥 deleteFlight()");

        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("responseMessage", "Admin access required");
            return "redirect:/flights/view";
        }
        boolean deleted = flightValidation.hardDeleteFlight(flightId);

        redirectAttributes.addFlashAttribute(
                "responseMessage",
                deleted ? "Flight permanently deleted" : "Delete failed"
        );

        return "redirect:/flights/view";
    }
    
    @GetMapping("/status")
    public String flightStatus(Model model) {
        model.addAttribute("flights", new ArrayList<>());
        return "flight-status"; 
    }
}
