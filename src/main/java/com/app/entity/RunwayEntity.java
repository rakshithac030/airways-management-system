package com.app.entity;

import javax.persistence.*;

import com.app.enums.RunwayStatus;

@Entity
@Table(
	    name = "runway",
	    uniqueConstraints = {
	        @UniqueConstraint(columnNames = {"airport_id", "runway_number"})
	    }
	)
public class RunwayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "runway_id")
    private Long runwayId;

    @Column(name = "runway_number", nullable = false)
    private String runwayNumber;

    @Column(name = "length")
    private double length;  // meters

    @Column(name = "surface_type")
    private String surfaceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "airport_id", nullable = false)
    private AirportEntity airport;
    
    @Enumerated(EnumType.STRING)
    private RunwayStatus status;


    // Default constructor
    public RunwayEntity() {}

    // Parameterized constructor
    public RunwayEntity(String runwayNumber, double length, String surfaceType, AirportEntity airport) {
        this.runwayNumber = runwayNumber;
        this.length = length;
        this.surfaceType = surfaceType;
        this.airport = airport;
    }

    // Getters and Setters
    public Long getRunwayId() {
        return runwayId;
    }

    public void setRunwayId(Long runwayId) {
        this.runwayId = runwayId;
    }

    public String getRunwayNumber() {
        return runwayNumber;
    }

    public void setRunwayNumber(String runwayNumber) {
        this.runwayNumber = runwayNumber;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public String getSurfaceType() {
        return surfaceType;
    }

    public void setSurfaceType(String surfaceType) {
        this.surfaceType = surfaceType;
    }

    public AirportEntity getAirport() {
        return airport;
    }

    public void setAirport(AirportEntity airport) {
        this.airport = airport;
    }
    
    @Override
    public String toString() {
        return "RunwayEntity{" +
                "id=" + runwayId +
                ", code='" + runwayNumber + '\'' +
                ", length=" + length +
                ", surface='" + surfaceType + '\'' +
                '}';
    }
}
