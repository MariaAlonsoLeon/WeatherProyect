package org.ulpgc.dacd.model;

import java.time.Instant;

public class HotelOffer {
    private String ss;
    private Instant ts;
    private String predictionTime;
    private String companyName;
    private float rate;
    private Location location;


    public HotelOffer(String companyName, float rate, Location location,String predictionTime) {
        this.ss = "hotel-provider";
        this.ts = Instant.now();
        this.companyName = companyName;
        this.rate = rate;
        this.location = location;
        this.predictionTime = predictionTime;
    }

}
