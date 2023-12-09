package org.ulpgc.dacd.control;

import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        if (args.length != 1) {
            logger.severe("Se requiere la clave de la API como argumento.");
            return;
        }
        String apiKey = args[0];
        WeatherSupplier weatherSupplier = new OpenWeatherMapSupplier(apiKey, "https://api.openweathermap.org/data/2.5/forecast?");
        WeatherStore weatherStore = new JMSWeatherStore("tcp://localhost:61616", "prediction.Weather", "PredictionProvider");
        WeatherController weatherController = new WeatherController(weatherSupplier, weatherStore);
        weatherController.execute();
    }

}