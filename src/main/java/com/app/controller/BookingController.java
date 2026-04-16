package com.app.controller;

import javax.servlet.http.HttpSession;
import static com.app.util.AuthUtil.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.entity.FlightEntity;
import com.app.entity.UserEntity;
import com.app.enums.BookingStatus;
import com.app.services.BookingValidation;
import com.app.services.FlightValidationService;


@Controller
@RequestMapping("/booking")
public class BookingController {

	private final BookingValidation bookingValidation;
    private final FlightValidationService flightValidationService;

    @Autowired
    public BookingController(
            BookingValidation bookingValidation, 
            FlightValidationService flightValidationService) {
        this.bookingValidation = bookingValidation;
        this.flightValidationService = flightValidationService;
    }
    
    private static final int MAX_SEATS_PER_BOOKING = 10;

    @PostMapping("/create")
    public String previewBooking(
            @RequestParam  Long flightId,
            @RequestParam Integer seats,
            HttpSession session,
            Model model,
            RedirectAttributes redirect) {
        UserEntity user = (UserEntity) session.getAttribute("loggedInUser");
        if (user == null) {
            redirect.addFlashAttribute("errorMessage", "Please login to book flights");
            return "redirect:/login?redirect=/flights/view";
        }
        
        if (seats > MAX_SEATS_PER_BOOKING) {
            redirect.addFlashAttribute("errorMessage", 
                String.format("Maximum %d seats allowed per booking", MAX_SEATS_PER_BOOKING));
            return "redirect:/flights/view";
        }

        FlightEntity flight = flightValidationService.getFlightById(flightId);        
        if (flight == null) {
            redirect.addFlashAttribute("errorMessage", "Flight not found");
            return "redirect:/flights/view";
        }
        if (seats > flight.getAvailableSeats()) {
            model.addAttribute("warningMessage", 
                String.format("Warning: Only %d seats available", flight.getAvailableSeats()));           
        }
        long hoursToDeparture =ChronoUnit.HOURS.between(LocalDateTime.now(), flight.getDepartureTime());
        
        model.addAttribute("flight", flight);
        model.addAttribute("seats", seats);
        model.addAttribute("maxSeats", MAX_SEATS_PER_BOOKING);

        return "booking-summary";
    }

    @PostMapping("/create-final")
    public String createBookingFinal(
            @RequestParam Long flightId,
            @RequestParam Integer seats,
            HttpSession session,
            RedirectAttributes redirect) {

        UserEntity user = (UserEntity) session.getAttribute("loggedInUser");
        if (user == null) {
            redirect.addFlashAttribute("errorMessage", "Session expired. Please login again");
            return "redirect:/login?redirect=/flights/view";
        }

        if (seats == null || seats <= 0) {
            redirect.addFlashAttribute("errorMessage", "Number of seats must be greater than zero");
            return "redirect:/flights/view";
        }

        if (seats > MAX_SEATS_PER_BOOKING) {
            redirect.addFlashAttribute("errorMessage", 
                String.format("Maximum %d seats allowed per booking", MAX_SEATS_PER_BOOKING));
            return "redirect:/flights/view";
        }

        try {
            boolean created = bookingValidation.createBooking(user, flightId, seats);
            
            if (created) {
                redirect.addFlashAttribute("successMessage", 
                    "Booking created successfully! Please confirm your booking in 'My Bookings'.");
                return "redirect:/booking/myBookings";
            } else {
                redirect.addFlashAttribute("errorMessage", 
                    "Booking creation failed. Please try again or contact support.");
                return "redirect:/flights/view";
            }
        } catch (IllegalArgumentException e) {
            // ✅ Catch business validation exceptions from service
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/flights/view";
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", 
                "An error occurred while creating booking. Please try again.");
            return "redirect:/flights/view";
        }
    }

    @PostMapping("/confirm")
    public String confirmBooking(
            @RequestParam Long bookingId,
            HttpSession session,
            RedirectAttributes redirect) {

        UserEntity user = (UserEntity) session.getAttribute("loggedInUser");

        if (user == null) {
            redirect.addFlashAttribute("responseMessage", "Login required");
            return "redirect:/login";
        }

        boolean confirmed =
            bookingValidation.confirmBooking(bookingId, user);

        redirect.addFlashAttribute(
            "responseMessage",
            confirmed ? "Booking confirmed" : "Confirm failed"
        );

        return "redirect:/booking/myBookings";
    }

    @PostMapping("/cancel")
    public String cancelBooking(
            @RequestParam Long bookingId,
            HttpSession session,
            Model model,
            RedirectAttributes redirect) {

        UserEntity user = (UserEntity) session.getAttribute("loggedInUser");

        if (user == null) {
            redirect.addFlashAttribute("responseMessage", "Login required");
            return "redirect:/login";
        }

        try{boolean cancelled =
            bookingValidation.cancelBooking(bookingId, user);
        model.addAttribute("now", LocalDateTime.now());


        redirect.addFlashAttribute(
            "responseMessage",
            cancelled ? "Booking cancelled" : "Cancel failed"
        );
    } catch (IllegalStateException e) {
        redirect.addFlashAttribute("responseMessage", e.getMessage());
    }
        

        return "redirect:/booking/myBookings";
    }

    @PostMapping("/admin/cancel")
    public String adminCancel(
            @RequestParam Long bookingId,
            HttpSession session,
            RedirectAttributes redirect) {

        if (!"ADMIN".equals(session.getAttribute("role"))) {
            redirect.addFlashAttribute("responseMessage", "Admin only");
            return "redirect:/index";
        }

        bookingValidation.adminCancelBooking(bookingId);
        redirect.addFlashAttribute("responseMessage", "Booking force-cancelled");

        return "redirect:/admin/bookings";
    }

    @GetMapping("/myBookings")
    public String myBookings(
            HttpSession session,
            Model model,
            RedirectAttributes redirect) {

        UserEntity user = (UserEntity) session.getAttribute("loggedInUser");

        if (user == null) {
            redirect.addFlashAttribute("responseMessage", "Please login first");
            return "redirect:/login";
        }

        model.addAttribute(
            "Bookings",
            bookingValidation.getBookingsByUser(user.getUserId())
        );

        return "bookings";
    }
    
    @GetMapping("/all")
    public String allBookings(HttpSession session, Model model, RedirectAttributes redirect) {

        if (!"ADMIN".equals(session.getAttribute("role"))) {
            redirect.addFlashAttribute("responseMessage", "Admin only");
            return "redirect:/index";
        }

        model.addAttribute("Bookings", bookingValidation.getAllBookings());
        return "bookings";
    }

    @PostMapping("/admin/update")
	 public String adminUpdateBooking(
	         @RequestParam Long bookingId,
	         @RequestParam BookingStatus status,
	         HttpSession session,
	         RedirectAttributes redirect
	 ) {
	     if (!isAdmin(session)) {
	         redirect.addFlashAttribute("responseMessage", "Admin only");
	         return "redirect:/booking/all";
	     }
	
	     boolean updated =
	             bookingValidation.updateBookingStatus(bookingId, status);
	
	     redirect.addFlashAttribute(
	         "responseMessage",
	         updated ? "Booking updated successfully" : "Update failed"
	     );
	
	     return "redirect:/booking/all";
	 }	 
}



