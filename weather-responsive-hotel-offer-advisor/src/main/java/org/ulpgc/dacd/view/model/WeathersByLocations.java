package org.ulpgc.dacd.view.model;

import java.util.List;

public record WeathersByLocations (String dt, String weatherType, List<WeatherByLocation> weatherByLocations) implements Output {
}
