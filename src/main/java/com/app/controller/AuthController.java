package com.app.controller;

import java.time.LocalDateTime;

import javax.servlet.http.HttpSession;
import javax.xml.bind.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.AirportDao.UserDao;
import com.app.entity.UserEntity;
import com.app.enums.RegistrationStatus;
import com.app.services.UserValidationService;

@Controller
public class AuthController {
	
	@Autowired
	private UserValidationService userValidationService;

    @Autowired
    private UserDao userDao;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           @RequestParam String email,
                           @RequestParam(required = false) String phoneNumber,
                           Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(password); 
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());
        user.setRegistrationStatus(RegistrationStatus.ACTIVE);

        try {
            userValidationService.validateUser(user, false);
            userDao.saveUser(user);
        } catch (ValidationException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
        
        

        model.addAttribute("responseMessage", "Registration successful. Please login.");
        return "login";
    }


    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        UserEntity user = userDao.findByUsername(username);

        if (user == null || !user.getPassword().equals(password)) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
        
        if (user.getRegistrationStatus() != RegistrationStatus.ACTIVE) {
            model.addAttribute("error", "Your account is not active");
            return "login";
        }


        session.setAttribute("loggedInUser", user);
        session.setAttribute("role", user.getRole());

        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

