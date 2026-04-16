package com.app.services;


import com.app.AirportDao.UserDao;
import com.app.entity.UserEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

import javax.xml.bind.ValidationException;

@Service
public class UserValidationService {

    private final UserDao userDao;

    @Autowired
    public UserValidationService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void validateUser(UserEntity user, boolean isUpdate) throws ValidationException {
        if (user == null) {
            throw new ValidationException("User object cannot be null");
        }

        validateUsername(user.getUsername());
        validateEmail(user.getEmail());
        validatePassword(user.getPassword());
        validatePhoneNumber(user.getPhoneNumber());

        if (!isUpdate) {
            checkUsernameUniqueness(user.getUsername());
            checkEmailUniqueness(user.getEmail());
        } else {
            UserEntity existingUser = userDao.findById(user.getUserId());
            if (existingUser != null) {
                if (!existingUser.getUsername().equals(user.getUsername())) {
                    checkUsernameUniqueness(user.getUsername());
                }
                if (!existingUser.getEmail().equals(user.getEmail())) {
                    checkEmailUniqueness(user.getEmail());
                }
            }
        }
    }

    private void validateUsername(String username) throws ValidationException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username is required");
        }
        if (username.length() < 3 || username.length() > 50) {
            throw new ValidationException("Username must be between 3 and 50 characters");
        }
    }

    private void validateEmail(String email)throws ValidationException {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!Pattern.compile(emailRegex).matcher(email).matches()) {
            throw new ValidationException("Invalid email format");
        }
    }

    private void validatePassword(String password)throws ValidationException {
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Password is required");
        }
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        if (!Pattern.compile(passwordRegex).matcher(password).matches()) {
            throw new ValidationException("Password must be at least 8 characters long and include uppercase, lowercase, digit, and special character");
        }
    }

    private void validatePhoneNumber(String phoneNumber)throws ValidationException {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return; 
        }
        String phoneRegex = "\\d{10,15}";
        if (!Pattern.compile(phoneRegex).matcher(phoneNumber).matches()) {
            throw new ValidationException("Phone number must contain 10 to 15 digits");
        }
    }

    private void checkUsernameUniqueness(String username)throws ValidationException {
        if (userDao.findByUsername(username) != null) {
            throw new ValidationException("Username '" + username + "' is already taken");
        }
    }

    private void checkEmailUniqueness(String email)throws ValidationException {
        if (userDao.findByEmail(email) != null) {
            throw new ValidationException("Email '" + email + "' is already registered");
        }
    }
}
