package com.app.dto;

import com.app.entity.FlightEntity;

public class ConnectingFlightDTO {
    private FlightEntity firstLeg;
    private FlightEntity secondLeg;

    public ConnectingFlightDTO(FlightEntity firstLeg, FlightEntity secondLeg) {
        this.firstLeg = firstLeg;
        this.secondLeg = secondLeg;
    }

    public FlightEntity getFirstLeg() { return firstLeg; }
    public FlightEntity getSecondLeg() { return secondLeg; }
}

