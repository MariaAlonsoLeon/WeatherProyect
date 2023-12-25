package org.ulpgc.dacd.model;

public record WeatherNode(String id, String city, double temperature, double rainProbability, int clouds, WeatherType weatherType){

}
