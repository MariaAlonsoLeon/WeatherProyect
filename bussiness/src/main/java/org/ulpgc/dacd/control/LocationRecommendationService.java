package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.WeatherNode;
import org.ulpgc.dacd.model.LocationNode;

import java.util.ArrayList;
import java.util.List;

public class LocationRecommendationService {
    private List<LocationNode> locations;

    public LocationRecommendationService(List<LocationNode> locations) {
        this.locations = locations;
    }

    public List<LocationNode> recommendLocations(WeatherNode weather) {
        List<LocationNode> recommendedLocations = new ArrayList<>();

        for (LocationNode location : locations) {
            if (isLocationSuitableForCondition(weather)) {
                recommendedLocations.add(location);
            }
        }

        return recommendedLocations;
    }

    private boolean isLocationSuitableForCondition(WeatherNode weather) {
        double temperature = weather.temperature();
        double rainProbability = weather.rainProbability();
        double cloudPercentage = weather.clouds();

        switch (weather.weatherType()) {
            case COLD:
                return temperature >= 0 && temperature <= 22;
            case WARM:
                return temperature > 22;
            case SNOWY:
                return temperature < 0;
            case RAINY:
                return rainProbability > 50;
            case CLEAR:
                return cloudPercentage < 30;
            default:
                return true; // Assume all locations are suitable if the weather is not recognized
        }
    }
}
