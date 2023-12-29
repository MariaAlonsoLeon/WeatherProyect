package org.ulpgc.dacd.control;

import org.ulpgc.dacd.control.exceptions.StoreException;
import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
    private final WeatherStore weatherStore;

    public WeatherController(WeatherSupplier weatherSupplier, WeatherStore weatherStore) {
        this.locations = loadLocations();
        this.weatherSupplier = weatherSupplier;
        this.weatherStore = weatherStore;
    }

    public void execute() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                fetchAndStoreWeather();
                logger.info("Weather data update completed.");
            }
        };
        timer.schedule(task, 0, 6 * 60 * 60 * 1000);
    }

    private void fetchAndStoreWeather() {
        try{
            for (Location location : locations) {
                for (Weather weather : weatherSupplier.getWeathers(location)) {
                    weatherStore.save(weather);
                }
            }
        } catch (StoreException e){
            logger.log(Level.SEVERE, "Error sending events: " + e.getMessage(), e);
        }
    }

    private static List<Location> loadLocations() {
        List<String> lines = readLinesFromFile("locations.tsv");
        return parseLinesToLocations(lines);
    }

    private static List<String> readLinesFromFile(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private static List<Location> parseLinesToLocations(List<String> lines) {
        List<Location> locations = new ArrayList<>();
        for (String line : lines) {
            Location location = parseLineToLocation(line);
            if (location != null) {
                locations.add(location);
            }
        }
        System.out.println(locations);
        return locations;
    }

    private static Location parseLineToLocation(String line) {
        String[] parts = line.split("\t");
        if (parts.length >= 3) {
            String name = parts[0];
            double latitude = parseDouble(parts[1]);
            double longitude = parseDouble(parts[2]);
            return new Location(name, latitude, longitude);
        }
        return null;
    }

    private static double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}
