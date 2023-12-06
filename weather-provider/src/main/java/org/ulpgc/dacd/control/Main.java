package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Location;

import java.util.ArrayList;
import java.util.List;
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
        JMSWeatherStore activeMQMessageSender = new JMSWeatherStore("tcp://localhost:61616");
        List<Location> locations = loadLocations();
        WeatherController weatherController = new WeatherController(days, locations, supplier, activeMQMessageSender);
    }

    private static List<Location> loadLocations() {
        List<Location> locations = new ArrayList<>();
        locations.add(new Location("Gran Canaria", 28.11, -15.43));
        locations.add(new Location("Tenerife", 28.46, -16.25));
        locations.add(new Location("La Gomera", 28.09, -17.1));
        locations.add(new Location("La Palma", 28.68, -17.76));
        locations.add(new Location("El Hierro", 27.64, -17.98));
        locations.add(new Location("Fuerteventura", 28.49, -13.86));
        locations.add(new Location("Lanzarote", 28.96, -13.55));
        locations.add(new Location("La Graciosa", 29.23, -13.5));
        return locations;
    }
}
