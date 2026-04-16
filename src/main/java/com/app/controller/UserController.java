	package com.app.controller;
	
	import java.util.List;
	
	import javax.xml.bind.ValidationException;
	import javax.servlet.http.HttpSession;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.stereotype.Controller;
	import org.springframework.ui.Model;
	import org.springframework.web.bind.annotation.*;
	import static com.app.util.AuthUtil.*;
	import com.app.entity.UserEntity;
	import com.app.services.UserValidationService;
	import com.app.AirportDao.UserDao;
	
	@Controller
	@RequestMapping("/user")
	public class UserController {
	
	    private final UserValidationService userValidationService;
	    private final UserDao userDao;
	
	    @Autowired
	    public UserController(UserValidationService userValidationService, UserDao userDao) {
	        System.out.println("Dispatcher servlet created UserController object");
	        this.userValidationService = userValidationService;
	        this.userDao = userDao;
	    }
	
	    @RequestMapping(value={"/", "/list"}, method = RequestMethod.GET)
	    public String listAllUsers(Model model) {
	        System.out.println("Invoked listAllUsers()");
	        List<UserEntity> users = userDao.findAll();
	        model.addAttribute("Users", users);
	        return "user";
	    }
	    
	    @RequestMapping(value="/save", method = RequestMethod.POST)
	    public String saveUser(@RequestParam String username,
	                           @RequestParam String password,@RequestParam String confirmPassword,
	                           @RequestParam String email,
	                           @RequestParam(required = false) String phoneNumber,
	                           @RequestParam String role,
	                           Model model) {
	
	        System.out.println("Invoked saveUser()");
	        UserEntity user = new UserEntity(username, password, email, phoneNumber, role);
	        try {
	            userValidationService.validateUser(user, false);
	            if (!password.equals(confirmPassword)) {
	                model.addAttribute("responseMessage", "Passwords do not match");
	                return "register";
	            }

	            boolean saved = userDao.saveUser(user);
	            model.addAttribute("responseMessage", saved ? "User saved successfully!" : "Failed to save user.");
	        } catch (Exception e) {
	            model.addAttribute("responseMessage", "Validation error: " + e.getMessage());
	            return "register";
	        }
	        return "redirect:/login";
	    }
	    
	    @RequestMapping(value="/searchById", method = RequestMethod.GET)
	    public String searchById(@RequestParam Long userId, Model model) {
	        System.out.println("Invoked searchById() with userId=" + userId);
	        UserEntity user = userDao.findById(userId);
	        if (user != null) {
	            model.addAttribute("USER", user);
	        } else {
	            model.addAttribute("responseMessage", "User not found with ID: " + userId);
	        }
	        model.addAttribute("Users", userDao.findAll());
	        return "user";
	    }
	
	    @RequestMapping(value="/searchByUsername", method = RequestMethod.GET)
	    public String searchByUsername(@RequestParam String username, Model model) {
	        System.out.println("Invoked searchByUsername() with username=" + username);
	        UserEntity user = userDao.findByUsername(username);
	        if (user != null) {
	            model.addAttribute("USER", user);
	        } else {
	            model.addAttribute("responseMessage", "User not found with username: " + username);
	        }
	        model.addAttribute("Users", userDao.findAll());
	        return "user";
	    }
	
	    @RequestMapping(value="/edit/{id}",method = RequestMethod.PUT)
	    public String editUser(@PathVariable Long id, Model model) {
	
	        UserEntity user = userDao.findById(id);
	
	        if (user == null) {
	            model.addAttribute("responseMessage", "User not found");
	        }
	
	        model.addAttribute("editUser", user);
	        model.addAttribute("Users", userDao.findAll());
	
	        return "user";
	    }
		
	    @RequestMapping(value ="/update" , method = RequestMethod.PUT)
	    public String updateUser(@RequestParam Long userId,
	                             @RequestParam String username,
	                             @RequestParam String password,
	                             @RequestParam String email,
	                             @RequestParam(required = false) String phoneNumber,
	                             @RequestParam String role,
	                             Model model) throws ValidationException {
	
	        System.out.println("Invoked updateUser() with userId=" + userId);
	        UserEntity user = userDao.findById(userId);
	        if (user != null) {
	            user.setUsername(username);
	            user.setPassword(password);
	            user.setEmail(email);
	            user.setPhoneNumber(phoneNumber);
	            user.setRole(role);
	            userValidationService.validateUser(user, true);
	            boolean updated = userDao.updateUser(user);
	            model.addAttribute("responseMessage", updated ? "User updated successfully!" : "Update failed.");
	        } else {
	            model.addAttribute("responseMessage", "User not found for update.");
	        }
	
	        model.addAttribute("Users", userDao.findAll());
	        return "user";
	    }
	
	    @RequestMapping(value="/delete/{id}", method = RequestMethod.DELETE)
	    public String deleteUser(@PathVariable Long id, HttpSession session, Model model) {
	        System.out.println("Invoked deleteUser() with id=" + id);
	        if (!"ADMIN".equals(session.getAttribute("role"))) {
	            model.addAttribute("responseMessage", "Access denied: Admin only");
	            model.addAttribute("Users", userDao.findAll());
	            return "user";
	        }
	        boolean deleted = userDao.deleteUser(id);
	        model.addAttribute("responseMessage", deleted ? "User deleted successfully!" : "Delete failed.");
	        model.addAttribute("Users", userDao.findAll());
	        return "user";
	    }
	    
	    @RequestMapping(value="/profile", method = RequestMethod.GET)
	    public String showProfile(HttpSession session, Model model) {
	        UserEntity user = (UserEntity) session.getAttribute("loggedInUser");
	        if (user == null) return "redirect:/login";
	        
	        model.addAttribute("user", user);
	        return "profile";
	    }

	    @RequestMapping(value="/profile/update", method = RequestMethod.POST)
	    public String updateProfile(
	        @RequestParam String email,
	        @RequestParam String phoneNumber,
	        HttpSession session) {
	        
	    	UserEntity user = (UserEntity) session.getAttribute("loggedInUser");
	    	if (user == null) return "redirect:/login";

	        user.setEmail(email);
	        user.setPhoneNumber(phoneNumber);
	        userDao.updateUser(user);
	        
	        session.setAttribute("loggedInUser", user);
	        return "redirect:/user/profile";
	    }
	    
	    @GetMapping("/forgot-password")
	    public String forgotPasswordForm() {
	        return "forgot-password";
	    }

	    @PostMapping("/forgot-password")
	    public String processForgotPassword(
	        @RequestParam String username,
	        Model model) {
	        
	        UserEntity user = userDao.findByUsername(username);
	        if (user == null) {
	            model.addAttribute("error", "User not found");
	            return "forgot-password";
	        }
	        String resetCode = "RESET-" + System.currentTimeMillis();
	        model.addAttribute("resetCode", resetCode);
	        model.addAttribute("username", username);
	        return "reset-password-instructions";
	    }

	
	}
	
