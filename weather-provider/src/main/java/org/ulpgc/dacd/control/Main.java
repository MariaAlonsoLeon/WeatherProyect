package org.ulpgc.dacd.control;

import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        if (args.length < 2) {
            logger.severe("The API key and the locations file path are required as arguments.");
            return;
        }
        String apiKey = args[0];
        WeatherSupplier weatherSupplier = new OpenWeatherMapSupplier(apiKey, "https://api.openweathermap.org/data/2.5/forecast?");
        WeatherStore weatherStore = new JMSWeatherStore("tcp://localhost:61616", "prediction.Weather", "weather-provider");
        WeatherController weatherController = new WeatherController(weatherSupplier, weatherStore, args[1]);
        weatherController.execute();
    }
}