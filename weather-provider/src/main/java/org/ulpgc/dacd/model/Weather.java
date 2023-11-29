package org.ulpgc.dacd.model;

import java.time.Instant;

public class Weather {
    private final Instant ts;
    private final String ss;
    private final Instant predictionTime;
    private final Location location;
    private final float temperature;
    private final int humidity;
    private final int clouds;
    private final float windSpeed;
    private final float rain;

    public Weather(String ss, Instant predictionTime, Location location,
                   float temperature, int humidity, int clouds, float windSpeed, float rain) {
        this.ts = Instant.now();
        this.ss = ss;
        this.predictionTime = predictionTime;
        this.location = location;
        this.temperature = temperature;
        this.humidity = humidity;
        this.clouds = clouds;
        this.windSpeed = windSpeed;
        this.rain = rain;
    }

    public Instant getTs() {
        return ts;
    }

    public String getSs() {
        return ss;
    }

    public Instant getPredictionTime() {
        return predictionTime;
    }

    public Location getLocation() {
        return location;
    }

    public float getTemperature() {
        return temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getClouds() {
        return clouds;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public float getRain() {
        return rain;
    }
}
