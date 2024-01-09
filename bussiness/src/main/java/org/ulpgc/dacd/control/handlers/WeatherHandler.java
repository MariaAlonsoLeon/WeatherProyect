package org.ulpgc.dacd.control.handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ulpgc.dacd.control.DataMartStore;
import org.ulpgc.dacd.model.WeatherRecord;
import org.ulpgc.dacd.model.WeatherType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WeatherHandler implements Handler {
    DataMartStore dataMartStore;

    public WeatherHandler(DataMartStore dataMartStore) {
        this.dataMartStore = dataMartStore;
    }

    @Override
    public void handleEvent(String message) {
        try {
            WeatherRecord weatherRecord = parseWeatherEvent(message);
            dataMartStore.saveWeather(weatherRecord);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WeatherRecord parseWeatherEvent(String message) {
        JsonObject weatherEventJson = parseJson(message);
        String formattedDate = extractAndFormatDate(weatherEventJson);
        JsonObject locationJson = weatherEventJson.getAsJsonObject("location");
        String locationName = locationJson.get("name").getAsString();
        int humidity = weatherEventJson.get("humidity").getAsInt();
        double temperature = weatherEventJson.get("temperature").getAsDouble();
        int clouds = weatherEventJson.get("clouds").getAsInt();
        float rain = weatherEventJson.get("rain").getAsFloat();
        float windSpeed = weatherEventJson.get("windSpeed").getAsFloat();
        WeatherType weatherType = determineWeatherType(temperature, rain, clouds);
        return new WeatherRecord(formattedDate, locationName, humidity, temperature, clouds, rain, windSpeed ,weatherType);
    }

    public String extractAndFormatDate(JsonObject weatherEventJson){
        String predictionTime = weatherEventJson.get("predictionTime").getAsString();
        LocalDateTime dateTime = LocalDateTime.parse(predictionTime, DateTimeFormatter.ISO_DATE_TIME);
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private JsonObject parseJson(String message) {
        try {
            JsonParser parser = new JsonParser();
            return parser.parse(message).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new JsonObject();
        }
    }

    public WeatherType determineWeatherType(double temperature, float rainProbability, int cloudPercentage) {
        if (temperature < 0) return WeatherType.SNOWY;
        if (temperature <= 18) return WeatherType.COLD;
        if (temperature > 18) return WeatherType.WARM;
        if (rainProbability > 50) return WeatherType.RAINY;
        if (cloudPercentage < 30) return WeatherType.CLEAR;
        return WeatherType.UNKNOWN;
    }
}