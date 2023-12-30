package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Modelo;
import org.ulpgc.dacd.model.WeatherType;

import java.util.Set;

public class LocationRecommendationService {
    private Modelo modelo;

    public LocationRecommendationService(Modelo modelo) {
        this.modelo = modelo;
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

    public Set<String> getLocationsByWeatherType(String weatherType, String date) {
        return modelo.getLocationsByWeatherTypeAndDate(weatherType, date);
    }

    public double getCheapestRate(String locationName, String date) {
        return modelo.getCheapestRate(locationName, date);
    }
}
