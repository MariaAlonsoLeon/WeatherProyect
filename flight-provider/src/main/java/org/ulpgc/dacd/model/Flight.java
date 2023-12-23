package org.ulpgc.dacd.model;

import java.time.Instant;

public class Flight {
    private Instant ts;
    private String ss;
    private String flightDate;
    private String flightStatus;
    private Location departure;
    private Location arrival;
    private String airline;
    private String flightNumber;

    public Flight(String flightDate, String flightStatus, Location departure, Location arrival,
                  String airline, String flightNumber) {
        this.ts = Instant.now();
        this.ss = "sensor";
        this.flightDate = flightDate;
        this.flightStatus = flightStatus;
        this.departure = departure;
        this.arrival = arrival;
        this.airline = airline;
        this.flightNumber = flightNumber;
    }

    public Instant getTs() {
        return ts;
    }

    public String getSs() {
        return ss;
    }

    public String getFlightDate() {
        return flightDate;
    }

    public String getFlightStatus() {
        return flightStatus;
    }

    public Location getDeparture() {
        return departure;
    }

    public void setDeparture(Location departure) {
        this.departure = departure;
    }

    public Location getArrival() {
        return arrival;
    }

    public String getAirline() {
        return airline;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "ts=" + ts +
                ", ss='" + ss + '\'' +
                ", flightDate=" + flightDate +
                ", flightStatus='" + flightStatus + '\'' +
                ", departure=" + departure +
                ", arrival=" + arrival +
                ", airline='" + airline + '\'' +
                ", flightNumber='" + flightNumber + '\'' +
                '}';
    }
}