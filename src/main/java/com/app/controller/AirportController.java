package com.app.controller;

import java.util.List;

import javax.servlet.http.HttpSession;
import static com.app.util.AuthUtil.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.entity.AirportEntity;
import com.app.enums.AirportStatus;
import com.app.services.AirportService;

@Controller
@RequestMapping("/airport")
public class AirportController {

    private final AirportService airportService;

    @Autowired
    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String listAirports(HttpSession session,Model model) {

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        List<AirportEntity> airports = airportService.getAllAirports();
        model.addAttribute("Airports", airports);
        model.addAttribute("airportStatuses", AirportStatus.values());
        return "airport";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createAirport(
            @RequestParam String airportName,
            @RequestParam String airportLocation,
            @RequestParam String iataCode,
            @RequestParam(defaultValue = "0") int parkingCapacity,
            @RequestParam(defaultValue = "false") boolean international,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {

        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("responseMessage", "Admin access required");
            return "redirect:/airport";
        }

        boolean saved = airportService.validateAndSaveAirport(
                airportName, airportLocation, iataCode, parkingCapacity, international
        );

        redirectAttributes.addFlashAttribute(
                "responseMessage",
                saved ? "Airport added successfully" : "Invalid airport data"
        );
        
        return "redirect:/airport";
    }

    @RequestMapping(value = "/searchById", method = RequestMethod.GET)
    public String searchById(
            @RequestParam(required = false) Long airportId,
            HttpSession session,
            Model model
    ) {
        if (!isAdmin(session)) {
            return "redirect:/airport";
        }

        if (airportId == null) {
            model.addAttribute("responseMessage", "Please enter Airport ID");
            model.addAttribute("Airports", airportService.getAllAirports());
        } else {
            AirportEntity airport = airportService.getAirportById(airportId);

            if (airport == null) {
                model.addAttribute("responseMessage", "Airport not found");
                model.addAttribute("Airports", airportService.getAllAirports());
            } else {
                model.addAttribute("Airports", List.of(airport));
            }
        }

        model.addAttribute("airportStatuses", AirportStatus.values());
        return "airport";
    }

    @RequestMapping(value = "/searchByName", method = RequestMethod.GET)
    public String searchByName(
            @RequestParam String airportName,
            @RequestParam String airportLocation,
            HttpSession session,
            Model model
    ) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        AirportEntity airport =
                airportService.getAirportByName(airportName, airportLocation);

        if (airport == null) {
            model.addAttribute("responseMessage", "Airport not found");
            model.addAttribute("Airports", airportService.getAllAirports());
        } else {
            model.addAttribute("Airports", List.of(airport));
        }

        if (isAdmin(session)) {
            model.addAttribute("airportStatuses", AirportStatus.values());
        }

        return "airport";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String updateAirport(
            @RequestParam Long airportId,
            @RequestParam String airportName,
            @RequestParam String airportLocation,
            @RequestParam int parkingCapacity,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("responseMessage", "Admin access required");
            return "redirect:/airport";
        }

        boolean updated = airportService.updateAirport(
                airportId, airportName, airportLocation, parkingCapacity
        );
        
        redirectAttributes.addFlashAttribute(
                "responseMessage",
                updated ? "Airport updated successfully" : "Update failed"
        );

        return "redirect:/airport";
    }

    
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    public String updateAirportStatus(
            @RequestParam Long airportId,
            @RequestParam AirportStatus status,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("responseMessage", "Admin access required");
            return "redirect:/airport";
        }

        boolean updated = airportService.updateAirportStatus(airportId, status);

        redirectAttributes.addFlashAttribute(
            "responseMessage",
            updated ? "Status updated successfully" : "Update failed"
        );

        return "redirect:/airport";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteAirport(
            @RequestParam Long airportId,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("responseMessage", "Admin access required");
            return "redirect:/airport";
        }

        boolean updated = airportService.updateAirportStatus(
                airportId, AirportStatus.CLOSED
        );

        redirectAttributes.addFlashAttribute(
                "responseMessage",
                updated ? "Airport closed successfully" : "Operation failed"
        );

        return "redirect:/airport";
    }
}
