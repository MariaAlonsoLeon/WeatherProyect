package org.ulpgc.dacd.control;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ulpgc.dacd.model.Modelo;
import org.ulpgc.dacd.model.WeatherNode;
import org.ulpgc.dacd.model.WeatherType;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WeatherHandler implements Handler {
    DataMartStore dataMartStore;

    public WeatherHandler(DataMartStore dataMartStore) {
        this.dataMartStore = dataMartStore;
    }

    @Override
    public void handleEvent(String eventData) {
        try {
            WeatherNode weatherNode = parseWeatherEvent(eventData);
            dataMartStore.saveWeather(weatherNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WeatherNode parseWeatherEvent(String eventData) {
        JsonObject weatherEventJson = parseJson(eventData);
        String formattedDate = extractAndFormatDate(weatherEventJson);
        JsonObject locationJson = weatherEventJson.getAsJsonObject("location");
        String locationName = locationJson.get("name").getAsString();
        int humidity = weatherEventJson.get("humidity").getAsInt();
        double temperature = weatherEventJson.get("temperature").getAsDouble();
        int clouds = weatherEventJson.get("clouds").getAsInt();
        float rain = weatherEventJson.get("rain").getAsFloat();
        WeatherType weatherType = determineWeatherType(temperature, rain, clouds);
        return new WeatherNode(formattedDate, locationName, humidity, temperature, clouds, rain, weatherType);
    }

    public String extractAndFormatDate(JsonObject weatherEventJson){
        String predictionTime = weatherEventJson.get("predictionTime").getAsString();
        LocalDateTime dateTime = LocalDateTime.parse(predictionTime, DateTimeFormatter.ISO_DATE_TIME);
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
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