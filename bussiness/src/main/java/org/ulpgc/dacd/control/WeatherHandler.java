package org.ulpgc.dacd.control;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ulpgc.dacd.model.Model;
import org.ulpgc.dacd.model.Weather;

import java.time.Instant;

import com.google.gson.Gson;

public class WeatherHandler implements Handler {
    @Override
    public void handleEvent(String message) {
        Weather weather = extractWeatherInfo(message);
        System.out.println("Handling weather event: " + weather);
    }

    private Weather extractWeatherInfo(String message) {
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
        Instant ts = Instant.parse(jsonObject.get("ts").getAsString());
        String condition = jsonObject.get("condition").getAsString();

        return new Weather(ts, condition);
    }
}