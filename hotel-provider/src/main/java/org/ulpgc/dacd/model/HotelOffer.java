package org.ulpgc.dacd.model;

import java.time.Instant;

public class HotelOffer {
    private String ss;
    private Instant ts;
    private String predictionTime;
    private String name;
    private float rate;
    private Location location;


    public HotelOffer(String name, float rate, Location location, String predictionTime) {
        this.ss = "hotel-provider";
        this.ts = Instant.now();
        this.name = name;
        this.rate = rate;
        this.location = location;
        this.predictionTime = predictionTime;
    }

    @Override
    public String toString() {
        return "HotelTaxes{" +
                "ss='" + ss + '\'' +
                ", ts=" + ts +
                ", predictionTime='" + predictionTime + '\'' +
                ", name='" + name + '\'' +
                ", tax=" + rate +
                ", location=" + location +
                '}';
    }
}
