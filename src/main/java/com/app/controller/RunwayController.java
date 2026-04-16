package com.app.controller;

import java.util.List;
import static com.app.util.AuthUtil.*;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.app.entity.RunwayEntity;
import com.app.services.RunwayValidation;

@Controller
@RequestMapping("/runways")
public class RunwayController {

    private final RunwayValidation runwayValidation;

    @Autowired
    public RunwayController(RunwayValidation runwayValidation) {
        this.runwayValidation = runwayValidation;
        System.out.println("RunwayController initialized by DispatcherServlet");
    }

    @RequestMapping(value = "/airportRunways", method = RequestMethod.GET)
    public String getRunwaysByAirport(@RequestParam Long airportId, HttpSession session,Model model) {

        System.out.println("Fetching runways for airportId: " + airportId);

        if (!isAdmin(session)) {
            System.out.println("Access denied: Not ADMIN");
            model.addAttribute("responseMessage", "Access denied: Admin only");
            return "redirect:/dashboard";
        }
        List<RunwayEntity> runways =
                runwayValidation.getRunwaysByAirportId(airportId);

		System.out.println("Runways fetched: " + runways);
		System.out.println("Runways size: " + (runways != null ? runways.size() : "NULL"));

        model.addAttribute("Runways", runways);
        model.addAttribute("AirportId", airportId);

        return "runways";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createRunway(@RequestParam String runwayNumber,
                               @RequestParam double length,
                               @RequestParam String surfaceType,
                               @RequestParam Long airportId,
                               HttpSession session,
                               Model model) {

        System.out.println("Creating runway: " + runwayNumber + ", AirportId: " + airportId);

        if (!isAdmin(session)) {
            System.out.println("Access denied: Not ADMIN");
            model.addAttribute("responseMessage", "Access denied: Admin only");
            populateRunways(model, airportId);
            return "runways";
        }

        boolean saved = runwayValidation
                .validateAndSaveRunway(runwayNumber, length, surfaceType, airportId);

        model.addAttribute("responseMessage",
                saved ? "Runway saved successfully" : "Invalid runway data");

        model.addAttribute("Runways",
                runwayValidation.getRunwaysByAirportId(airportId));
        model.addAttribute("AirportId", airportId);

        return "runways";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String editRunway(@RequestParam Long runwayId,
                             @RequestParam Long airportId,
                             HttpSession session,
                             Model model) {

        System.out.println("Editing runwayId: " + runwayId);

        RunwayEntity runway = runwayValidation.getRunwayById(runwayId);

        if (!isAdmin(session)) {
            System.out.println("Access denied while editing runway");
            model.addAttribute("responseMessage", "Access denied: Admin only");
            populateRunways(model, airportId);
            return "runways";
        }

        if (runway == null) {
            System.out.println("Runway not found");
            model.addAttribute("responseMessage", "Runway not found");
        }

        model.addAttribute("editRunway", runway);
        model.addAttribute("Runways",
                runwayValidation.getRunwaysByAirportId(airportId));
        model.addAttribute("AirportId", airportId);

        return "runways";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String updateRunway(@RequestParam Long runwayId,
                               @RequestParam String runwayNumber,
                               @RequestParam double length,
                               @RequestParam String surfaceType,
                               @RequestParam Long airportId,
                               HttpSession session,
                               Model model) {

        System.out.println("Updating runwayId: " + runwayId);

        if (!isAdmin(session)) {
            System.out.println("Access denied while updating runway");
            model.addAttribute("responseMessage", "Access denied: Admin only");
            populateRunways(model, airportId);
            return "runways";
        }

        boolean updated = runwayValidation.updateRunway(
                runwayId, runwayNumber, length, surfaceType, airportId);

        model.addAttribute("responseMessage",
                updated ? "Runway updated successfully" : "Update failed");

        model.addAttribute("Runways",
                runwayValidation.getRunwaysByAirportId(airportId));
        model.addAttribute("AirportId", airportId);

        return "runways";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteRunway(@RequestParam Long runwayId,
                               @RequestParam Long airportId,
                               HttpSession session,
                               Model model) {

        System.out.println("Deleting runwayId: " + runwayId);

        if (!isAdmin(session)) {
            System.out.println("Access denied while deleting runway");
            model.addAttribute("responseMessage", "Access denied: Admin only");
            populateRunways(model, airportId);
            return "runways";
        }

        boolean deleted = runwayValidation.deleteRunway(runwayId);

        model.addAttribute("responseMessage",
                deleted ? "Runway deleted successfully" : "Delete failed");

        model.addAttribute("Runways",
                runwayValidation.getRunwaysByAirportId(airportId));
        model.addAttribute("AirportId", airportId);

        return "runways";
    }

    private void populateRunways(Model model, Long airportId) {
        model.addAttribute("Runways",
                runwayValidation.getRunwaysByAirportId(airportId));
        model.addAttribute("AirportId", airportId);
    }
}
