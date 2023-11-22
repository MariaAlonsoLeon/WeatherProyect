package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WeatherController {
    private static final Logger logger = Logger.getLogger(WeatherController.class.getName());
    private final List<Location> locations;
    private final int days;
    private final WeatherSupplier weatherSupplier;
    private final WeatherStore weatherStore;
    private boolean firstTime = true;
    private final Scanner scanner;

    public WeatherController(int days, WeatherSupplier weatherSupplier, WeatherStore weatherStore) {
        this.days = days;
        this.weatherSupplier = weatherSupplier;
        this.weatherStore = weatherStore;
        this.locations = loadLocations();
        this.scanner = new Scanner(System.in);
    }

    public void execute() throws IOException {
        if (firstTime) {
            updateWeatherData();
            firstTime = false;
        }
        showWeatherForUserInput();
    }

    private void updateWeatherData() {
        Instant currentTime = Instant.now();
        List<Instant> forecastTimes = calculateForecastTimes(currentTime, days);
        for (Location location : locations) {
            try {
                processLocation(location, forecastTimes);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error processing location: " + e.getMessage(), e);
            }
        }
        logger.info("Weather data update completed.");
    }

    private void showWeatherForUserInput() {
        try {
            System.out.print("Enter the name of the island: ");
            String userInputLocationName = scanner.nextLine();
            System.out.print("Enter the date in 'yyyy-MM-dd' format (for example, 2023-10-30): ");
            String userInputDateString = scanner.nextLine();
            Instant userInputDate = parseUserInputDate(userInputDateString);
            findAndDisplayWeather(userInputLocationName, userInputDate);
        } catch (Exception e) {
            System.out.println("Incorrect Data.");
        }
    }

    private Instant parseUserInputDate(String userInputDateString) throws DateTimeParseException {
        return Instant.parse(userInputDateString + "T12:00:00Z");
    }

    private void findAndDisplayWeather(String userInputLocationName, Instant userInputDate) {
        try {
            findLocationByName(userInputLocationName)
                    .ifPresentOrElse(
                            userLocation -> displayWeatherData(userLocation, userInputDate),
                            () -> System.out.println("No location found for the provided name: " + userInputLocationName)
                    );
        } catch (Exception e) {
            System.out.println("Error finding location: " + e.getMessage());
        }
    }

    private void displayWeatherData(Location userLocation, Instant userInputDate) {
        try {
            weatherStore.loadWeather(userLocation, userInputDate)
                    .ifPresentOrElse(
                            userWeatherData -> {
                                System.out.println("Weather data for " + userLocation.getName() + " on " + userWeatherData.getTs() + ":");
                                System.out.println(userWeatherData);
                            },
                            () -> System.out.println("No weather data found for " + userLocation.getName() + " on " + userInputDate)
                    );
        } catch (Exception e) {
            System.out.println("Error loading weather data: " + e.getMessage());
        }
    }

    private Optional<Location> findLocationByName(String name) {
        return locations.stream()
                .filter(location -> location.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    private void processLocation(Location location, List<Instant> forecastTimes) {
        try {
            List<Weather> weathers = weatherSupplier.getWeathers(location, forecastTimes);
            if (weathers != null && !weathers.isEmpty()) {
                weatherStore.save(weathers);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error getting weather for location " + location.getName(), e);
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
