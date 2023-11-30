package org.ulpgc.dacd.control;

import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final int days = 5;

    public static void main(String[] args) {
        if (args.length != 1) {
            logger.severe("Se requiere la clave de la API como argumento.");
            return;
        }
        String apiKey = args[0];
        WeatherSupplier supplier = new OpenWeatherMapSupplier("https://api.openweathermap.org/data/2.5/forecast?", apiKey);
        ActiveMQMessageSender activeMQMessageSender = new ActiveMQMessageSender("tcp://localhost:61616");
        WeatherController weatherControl = new WeatherController(days, supplier, activeMQMessageSender);
    }
}
