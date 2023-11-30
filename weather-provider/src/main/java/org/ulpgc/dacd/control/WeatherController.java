package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WeatherController {
    private static final Logger logger = Logger.getLogger(WeatherController.class.getName());
    private final List<Location> locations;
    private final int days;
    private final WeatherSupplier weatherSupplier;
    private final ActiveMQMessageSender messageSender;

    public WeatherController(int days, WeatherSupplier weatherSupplier, ActiveMQMessageSender messageSender) {
        this.days = days;
        this.weatherSupplier = weatherSupplier;
        this.locations = loadLocations();
        this.messageSender = messageSender;

        // Iniciar la tarea periódica
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::execute, 0, 1, TimeUnit.MINUTES);
    }

    public void execute() {
        Instant currentTime = Instant.now();
        List<Instant> forecastTimes = calculateForecastTimes(currentTime, days);
        List<Weather> weathers = new ArrayList<>();
        unifyWeatherLists(weathers, forecastTimes);
        sendEvents(weathers);
        logger.info("Weather data update completed.");
    }

    private List<Weather> unifyWeatherLists(List<Weather> weathers, List<Instant> forecastTimes) {
        for (Location location : locations) {
            try {
                for (Weather weather : processLocation(location, forecastTimes)) {
                    weathers.add(weather);
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error processing location: " + e.getMessage(), e);
            }
        }
        return weathers;
    }

    private List<Weather> processLocation(Location location, List<Instant> forecastTimes) throws IOException {
        List<Weather> weathers = weatherSupplier.getWeathers(location, forecastTimes);
        return weathers;
    }

    private void sendEvents(List<Weather> weathers) {
        if (weathers != null && !weathers.isEmpty()) {
            messageSender.sendMessage(weathers);
        }
    }

    private List<Instant> calculateForecastTimes(Instant currentTime, int days) {
        return IntStream.range(0, days)
                .mapToObj(i -> currentTime.plus(i + 1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS).plus(12, ChronoUnit.HOURS))
                .collect(Collectors.toList());
    }

    private List<Location> loadLocations() {
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
