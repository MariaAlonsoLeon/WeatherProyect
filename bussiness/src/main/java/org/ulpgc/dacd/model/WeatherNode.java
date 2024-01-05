package org.ulpgc.dacd.model;

public record WeatherNode(String predictionTime, String location, int humidity, double temperature, int clouds, double rainProbability, float windSpeed, WeatherType weatherType){

}
