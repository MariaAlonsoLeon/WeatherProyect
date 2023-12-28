package org.ulpgc.dacd.control;

import org.neo4j.driver.Record;
import org.ulpgc.dacd.model.LocationNode;
import org.ulpgc.dacd.model.Modelo;
import org.ulpgc.dacd.model.WeatherNode;
import org.ulpgc.dacd.model.WeatherType;
import org.neo4j.driver.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LocationRecommendationService {
    private List<String> locations;
    private Modelo modelo;

    public LocationRecommendationService(Modelo modelo) {
        this.locations = locations;
        this.modelo = modelo;
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
                return true;
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

    public Set<String> obtenerLocalizacionesPorTipoClima(String weatherType) {
        return modelo.obtenerLocalizacionesPorTipoClima(weatherType);
    }

    public double obtenerTarifaMasBarata(String locationName, String date){
        return modelo.obtenerTarifaMasBarata(locationName, date);
    }
}
