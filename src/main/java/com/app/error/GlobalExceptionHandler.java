package com.app.error;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            NumberFormatException.class,
            IllegalArgumentException.class
    })
    public Object handleBadRequest(Exception ex, HttpServletRequest req) {
        log.warn("Bad request: {} on {}", ex.getMessage(), req.getRequestURI());


        ModelAndView mv = new ModelAndView("error");
        mv.addObject("title", "Invalid Request");
        mv.addObject("message", "There was a problem with the request parameters: " + ex.getMessage());
        mv.addObject("path", req.getRequestURI());
        mv.addObject("status", 400);
        return mv;
    }
    @ExceptionHandler(NullPointerException.class)
    public Object handleNPE(NullPointerException ex, HttpServletRequest req) {
        log.error("NullPointerException at " + req.getRequestURI(), ex);


        ModelAndView mv = new ModelAndView("error");
        mv.addObject("title", "Required data missing");
        mv.addObject("message", "Some required data was missing or invalid. If you believe this is a bug, contact support.");
        mv.addObject("path", req.getRequestURI());
        mv.addObject("status", 400);
        return mv;
    }

    @ExceptionHandler(Exception.class)
    public Object handleAll(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception at " + req.getRequestURI(), ex);


        ModelAndView mv = new ModelAndView("error");
        mv.addObject("title", "Something went wrong");
        mv.addObject("message", "An unexpected error occurred. We logged the issue — try again, or contact support.");
        mv.addObject("path", req.getRequestURI());
        mv.addObject("status", 500);
        return mv;
    }
}
