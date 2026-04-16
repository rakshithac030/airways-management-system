package com.app.enums;

public enum FlightStatus {
    SCHEDULED,   // future flight
    DELAYED,     // delayed but still happening
    CANCELLED,   // cancelled
    DEPARTED,
    COMPLETED, 
    LANDED// past flight (optional, future use)
}

