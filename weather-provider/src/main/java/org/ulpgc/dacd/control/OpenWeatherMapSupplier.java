package org.ulpgc.dacd.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.JsonElement;

public class OpenWeatherMapSupplier implements WeatherSupplier {
    private final String apiKey;
    private final String templateUrl;
    private static final Logger logger = Logger.getLogger(OpenWeatherMapSupplier.class.getName());
    private static final String LIST_KEY = "list";
    private static final String TIMESTAMP_KEY = "dt";
    private static final String HOUR_AT_12 = "12:00:00";

    public OpenWeatherMapSupplier(String apiKey, String templateUrl) {
        this.apiKey = apiKey;
        this.templateUrl = templateUrl;
    }

    @Override
    public List<Weather> getWeathers(Location location) {
        String url = buildUrl(location);
        return parseWeatherData(url, location);
    }

    private String buildUrl(Location location) {
        String coordinates = String.format("lat=%s&lon=%s", location.lat(), location.lon());
        return String.format("%s%s&appid=%s&units=metric", templateUrl, coordinates, apiKey);
    }

    private List<Weather> parseWeatherData(String url, Location location) {
        try {
            String jsonWeather = getWeatherFromUrl(url);
            JsonObject jsonObject = JsonParser.parseString(jsonWeather).getAsJsonObject();
            JsonArray list = jsonObject.getAsJsonArray(LIST_KEY);
            return list != null ? findMatchingWeatherItems(list, location) : new ArrayList<>();
        } catch (IOException e) {
            handleException("Error fetching or parsing weather data", e);
            return new ArrayList<>();
        }
    }

    private List<Weather> findMatchingWeatherItems(JsonArray list, Location location) {
        List<Weather> matchingWeatherItems = new ArrayList<>();
        for (JsonElement element : list) {
            if (element.isJsonObject()) {
                JsonObject weatherItem = element.getAsJsonObject();
                if (isWeatherAt12(weatherItem)) {
                    Weather weather = createWeatherFromForecastData(weatherItem, location);
                    if (weather != null) {
                        matchingWeatherItems.add(weather);
                    }
                }
            }
        }
        return matchingWeatherItems;
    }

    private boolean isWeatherAt12(JsonObject weatherItem) {
        String hour = weatherItem.get("dt_txt").getAsString().substring(11, 19);
        return hour.equals(HOUR_AT_12);
    }

    private Weather createWeatherFromForecastData(JsonObject forecastData, Location location) {
        try {
            JsonObject main = forecastData.getAsJsonObject("main");
            JsonObject cloud = forecastData.getAsJsonObject("clouds");
            JsonObject wind = forecastData.getAsJsonObject("wind");
            float temperature = main.get("temp").getAsFloat();
            int humidity = main.get("humidity").getAsInt();
            int clouds = cloud.get("all").getAsInt();
            float windSpeed = wind.get("speed").getAsFloat();
            float rainProbability = forecastData.get("pop").getAsFloat();

            Instant instant = Instant.ofEpochSecond(forecastData.get(TIMESTAMP_KEY).getAsLong());
            return new Weather("WeatherProvider/OpenWeatherMap", instant, location, temperature, humidity, clouds, windSpeed, rainProbability);
        } catch (Exception e) {
            handleException("Error creating Weather object", e);
            return null;
        }
    }

    private String getWeatherFromUrl(String url) throws IOException {
        Document document = Jsoup.connect(url).ignoreContentType(true).get();
        return document.text();
    }

    private void handleException(String message, Exception e) {
        logger.log(Level.SEVERE, message, e);
    }
}
