package org.ulpgc.dacd.control;

import org.ulpgc.dacd.control.exceptions.WeatherDataException;
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
    private final JMSWeatherStore jmsWeatherStore;

    public WeatherController(int days, List<Location> locations, WeatherSupplier weatherSupplier, JMSWeatherStore jmsWeatherStore) {
        this.days = days;
        this.locations = locations;
        this.weatherSupplier = weatherSupplier;
        this.jmsWeatherStore = jmsWeatherStore;

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::execute, 0, 1, TimeUnit.MINUTES);
    }

    public void execute() {
        Instant currentTime = Instant.now();
        List<Instant> forecastTimes = calculateForecastTimes(currentTime, days);
        sendEvents(unifyWeatherLists(forecastTimes));
        logger.info("Weather data update completed.");
    }

    private List<Weather> unifyWeatherLists(List<Instant> forecastTimes) {
        List<Weather> weathers = new ArrayList<>();
        for (Location location : locations) {
            try {
                weathers.addAll(processLocation(location, forecastTimes));
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error processing location: " + e.getMessage(), e);
            }
        }
        return weathers;
    }

    private List<Weather> processLocation(Location location, List<Instant> forecastTimes) throws WeatherDataException {
        return weatherSupplier.getWeathers(location, forecastTimes);
    }

    private void sendEvents(List<Weather> weathers) {
        for (Weather weather : weathers) {
            jmsWeatherStore.sendMessage(weather);
        }
    }

    private List<Instant> calculateForecastTimes(Instant currentTime, int days) {
        return IntStream.range(0, days)
                .mapToObj(i -> currentTime.plus(i + 1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS).plus(12, ChronoUnit.HOURS))
                .collect(Collectors.toList());
    }
}
