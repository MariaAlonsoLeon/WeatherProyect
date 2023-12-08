package org.ulpgc.dacd.control;

import org.ulpgc.dacd.control.exceptions.StoreException;
import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherController {
    private static final Logger logger = Logger.getLogger(WeatherController.class.getName());
    private final List<Location> locations;
    private final WeatherSupplier weatherSupplier;
    private final JMSWeatherStore jmsWeatherStore;

    public WeatherController(WeatherSupplier weatherSupplier, JMSWeatherStore jmsWeatherStore) {
        this.locations = loadLocations();
        this.weatherSupplier = weatherSupplier;
        this.jmsWeatherStore = jmsWeatherStore;
    }

    public void execute() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                List<Weather> weathers = unifyWeatherLists();
                sendEvents(weathers);
                logger.info("Weather data update completed.");
            }
        };
        timer.schedule(task, 0, 1 * 60 * 1000);
    }

    private List<Weather> unifyWeatherLists() {
        List<Weather> weathers = new ArrayList<>();
        for (Location location : locations) {
            try {
                weathers.addAll(weatherSupplier.getWeathers(location));
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error processing location: " + e.getMessage(), e);
            }
        }
        System.out.println(weathers);
        return weathers;
    }

    private void sendEvents(List<Weather> weathers) {
        try {
            for (Weather weather : weathers) {
                jmsWeatherStore.save(weather);
            }
        } catch (StoreException e) {
            logger.log(Level.SEVERE, "Error sending events: " + e.getMessage(), e);
        }
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
