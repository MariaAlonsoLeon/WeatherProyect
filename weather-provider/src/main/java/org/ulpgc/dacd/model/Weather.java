package org.ulpgc.dacd.model;

import java.time.Instant;

public class Weather {
    private Instant ts;
    private String ss;
    private Instant predictionTime;
    private Location location;
    private float temperature;
    private int humidity;
    private int clouds;
    private float windSpeed;
    private float rain;

    public Weather(Instant predictionTime, Location location,
                   float temperature, int humidity, int clouds, float windSpeed, float rain) {
        this.ts = Instant.now();
        this.ss = "weather-provider";
        this.predictionTime = predictionTime;
        this.location = location;
        this.temperature = temperature;
        this.humidity = humidity;
        this.clouds = clouds;
        this.windSpeed = windSpeed;
        this.rain = rain;
    }
}
