package com.app.controller;

import static com.app.util.AuthUtil.isAdmin;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.services.AdminDashboardService;

@Controller
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService dashboardService;

    @GetMapping("/admin/dashboard")
    public String adminDashboard(
            HttpSession session,
            Model model,
            RedirectAttributes redirect
    ) {

        if (!isAdmin(session)) {
            redirect.addFlashAttribute("responseMessage", "Admin access only");
            return "redirect:/dashboard";
        }

        model.addAttribute("totalUsers", dashboardService.totalUsers());
        model.addAttribute("totalAirports", dashboardService.totalAirports());
        model.addAttribute("totalFlights", dashboardService.totalFlights());
        model.addAttribute("totalBookings", dashboardService.totalBookings());

        model.addAttribute("confirmedBookings", dashboardService.confirmedBookings());
        model.addAttribute("pendingBookings", dashboardService.pendingBookings());
        model.addAttribute("cancelledBookings", dashboardService.cancelledBookings());

        model.addAttribute("activeFlights",
        		dashboardService.countActive());

        model.addAttribute("delayedFlights",
        		dashboardService.countDelayed());

        model.addAttribute("cancelledFlights",
        		dashboardService.countCancelled());

        model.addAttribute("recentFlights",
        		dashboardService.getRecentActivityFlights());

        model.addAttribute("seatsSoldToday", dashboardService.seatsSoldToday());
        model.addAttribute("flightsDepartingToday", dashboardService.flightsDepartingToday());

        return "admin-dashboard";
    }
}
