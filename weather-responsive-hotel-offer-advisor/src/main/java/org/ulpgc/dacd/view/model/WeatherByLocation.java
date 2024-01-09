package org.ulpgc.dacd.view.model;

public record WeatherByLocation(String locationName, float temperature, float rain, float windSpeed, int humidity) implements Output{
}
