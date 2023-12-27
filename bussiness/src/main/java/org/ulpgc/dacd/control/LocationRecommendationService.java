package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.LocationNode;
import org.ulpgc.dacd.model.WeatherNode;
import org.ulpgc.dacd.model.WeatherType;

import java.util.ArrayList;
import java.util.List;

public class LocationRecommendationService {
    private List<String> locations;

    public LocationRecommendationService() {
        this.locations = locations;
    }

    public List<String> recommendLocationsByWeatherType(List<WeatherNode> weatherData, WeatherType weatherType) {
        List<String> recommendedLocations = new ArrayList<>();

        for (WeatherNode weather : weatherData) {
            if (isLocationSuitableForCondition(weather, weatherType)) {
                recommendedLocations.add(weather.location());
            }
        }

        return recommendedLocations;
    }

    private boolean isLocationSuitableForCondition(WeatherNode weather, WeatherType weatherType) {
        double temperature = weather.temperature();
        double rainProbability = weather.rainProbability();
        double cloudPercentage = weather.clouds();

        switch (weatherType) {
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

    public WeatherType determineWeatherType(double temperature, float rainProbability, int cloudPercentage) {
        if (temperature >= 0 && temperature <= 22) {
            return WeatherType.COLD;
        } else if (temperature > 22) {
            return WeatherType.WARM;
        } else if (temperature < 0) {
            return WeatherType.SNOWY;
        } else if (rainProbability > 50) {
            return WeatherType.RAINY;
        } else if (cloudPercentage < 30) {
            return WeatherType.CLEAR;
        } else {
            return WeatherType.UNKNOWN;
        }
    }
}
