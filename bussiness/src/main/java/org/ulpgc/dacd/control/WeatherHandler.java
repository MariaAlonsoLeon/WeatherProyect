package org.ulpgc.dacd.control;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ulpgc.dacd.model.Modelo;
import org.ulpgc.dacd.model.WeatherNode;
import org.ulpgc.dacd.model.WeatherType;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WeatherHandler implements Handler {
    private final Modelo modelo;
    private final LocationRecommendationService locationRecommendationService;

    public WeatherHandler(Modelo modelo) {
        this.modelo = modelo;
        this.locationRecommendationService = new LocationRecommendationService();
    }

    @Override
    public void handleEvent(String eventData) {
        try {
            WeatherNode weatherNode = parseWeatherEvent(eventData);
            updateModelWithWeatherNode(weatherNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WeatherNode parseWeatherEvent(String eventData) {
        JsonObject weatherEventJson = parseJson(eventData);

        String predictionTime = weatherEventJson.get("predictionTime").getAsString();
        JsonObject locationJson = weatherEventJson.getAsJsonObject("location");
        String locationName = locationJson.get("name").getAsString();
        int humidity = weatherEventJson.get("humidity").getAsInt();
        double temperature = weatherEventJson.get("temperature").getAsDouble();
        int clouds = weatherEventJson.get("clouds").getAsInt();
        float rain = weatherEventJson.get("rain").getAsFloat();
        WeatherType weatherType = locationRecommendationService.determineWeatherType(temperature, rain, clouds);
        return new WeatherNode(predictionTime, locationName, humidity, temperature, clouds, rain, weatherType);
    }

    private void updateModelWithWeatherNode(WeatherNode weatherNode) {
        modelo.updateWeatherNode(weatherNode);
    }

    private JsonObject parseJson(String jsonData) {
        try {
            JsonParser parser = new JsonParser();
            return parser.parse(jsonData).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new JsonObject();
        }
    }
}