package org.ulpgc.dacd.model;

import java.time.Instant;

public class HotelTaxes {
    private String ss;
    private Instant ts;
    private String name;
    private float tax;
    private Location location;


    public HotelTaxes(String name, float tax, Location location) {
        this.ss = "hotel-provider";
        this.ts = Instant.now();
        this.name = name;
        this.tax = tax;
        this.location = location;

    }
}
