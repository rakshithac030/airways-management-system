package com.app.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import com.app.enums.AirportStatus;

@Entity
@Table(name = "airport",
uniqueConstraints = {
    @UniqueConstraint(columnNames = {"airport_name", "airport_location"})
})
public class AirportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "airport_id")
    private Long airportId;

    @Column(name = "airport_name", nullable = false)
    private String airportName;

    @Column(name = "airport_location", nullable = false)
    private String airportLocation;

    @Column(name = "iata_code", nullable = false, length = 3)
    private String iataCode;

    @Column(name = "parking_capacity")
    private int parkingCapacity;

    @Column(name = "international", nullable = false)
    private boolean international;
    
    @Column(name = "airport_code", unique = true, nullable = false)
    private String airportCode;


    @OneToMany(
    	    mappedBy = "airport",
    	    fetch = FetchType.LAZY,
    	    cascade = CascadeType.ALL,
    	    orphanRemoval = true
    	)
    private List<RunwayEntity> runways = new ArrayList<>();

@Enumerated(EnumType.STRING)
@Column(nullable = false)
private AirportStatus status = AirportStatus.ACTIVE;


    // Default constructor
    public AirportEntity() {}

    // Parameterized constructor
    public AirportEntity(String airportName, String airportLocation, String iataCode,  int parkingCapacity, boolean international) {
        this.airportName = airportName;
        this.airportLocation = airportLocation;
        this.iataCode = iataCode;
        this.parkingCapacity = parkingCapacity;
        this.international = international;
    }    


	// Getters and Setters
    public Long getAirportId() {
        return airportId;
    }

    public void setAirportId(Long airportId) {
        this.airportId = airportId;
    }

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public String getAirportLocation() {
        return airportLocation;
    }

    public void setAirportLocation(String airportLocation) {
        this.airportLocation = airportLocation;
    }

    public String getIataCode() {
        return iataCode;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    public int getParkingCapacity() {
        return parkingCapacity;
    }

    public void setParkingCapacity(int parkingCapacity) {
        this.parkingCapacity = parkingCapacity;
    }

    public boolean isInternational() {
        return international;
    }

    public void setInternational(boolean international) {
        this.international = international;
    }
    
    public String getAirportCode() {
        return airportCode;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }


    public List<RunwayEntity> getRunways() {
        return runways;
    }

    public void setRunways(List<RunwayEntity> runways) {
        this.runways = runways;
    }
    
    
    public AirportStatus getStatus() {
        return status;
    }

    public void setStatus(AirportStatus status) {
        this.status = status;
    }

    @Transient
    public int getRunwayCount() {
        return runways == null ? 0 : runways.size();
    }
    
    public void addRunway(RunwayEntity r){
        runways.add(r);
        r.setAirport(this);
      }
      public void removeRunway(RunwayEntity r){
        runways.remove(r);
        r.setAirport(null);
      }
      
      @Override
      public String toString() {
          return "AirportEntity{" +
                  "id=" + airportId +
                  ", name='" + airportName + '\'' +
                  ", location='" + airportLocation + '\'' +
                  ", iata='" + iataCode + '\'' +
                  ", code='" + airportCode + '\'' +
                  ", parking=" + parkingCapacity +
                  ", international=" + international +
                  ", status=" + status +
                  ", runwayCount=" + (runways != null ? runways.size() : 0) +
                  '}';
      }
}
