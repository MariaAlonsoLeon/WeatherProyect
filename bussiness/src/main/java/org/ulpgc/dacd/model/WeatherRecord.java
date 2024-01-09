package org.ulpgc.dacd.model;

public record WeatherRecord(String predictionTime, String location, int humidity, double temperature, int clouds, double rainProbability, float windSpeed, WeatherType weatherType){

}
