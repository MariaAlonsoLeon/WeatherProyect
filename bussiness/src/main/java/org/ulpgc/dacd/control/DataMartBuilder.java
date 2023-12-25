package org.ulpgc.dacd.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.ulpgc.dacd.model.*;

import java.util.List;

public class DataMartBuilder {
    private DataLakeAccessor dataLakeAccessor;
    private FlightHandler flightHandler;
    private WeatherHandler weatherHandler;

    private Modelo modelo; // Instead of Map<String, String> dataMart
    private List<LocationNode> locations; // List of available locations

    public DataMartBuilder(DataLakeAccessor dataLakeAccessor, FlightHandler flightHandler, WeatherHandler weatherHandler, List<LocationNode> locations) {
        this.dataLakeAccessor = dataLakeAccessor;
        this.flightHandler = flightHandler;
        this.weatherHandler = weatherHandler;
        this.locations = locations;
        this.modelo = new Modelo();
    }

    public void buildDataMart() {
        loadDataFromDataLake();
        subscribeToEventTopics();
    }

    private void loadDataFromDataLake() {
        // Load initial data from DataLake
        List<String> weatherData = dataLakeAccessor.getWeatherData();
        List<String> flightData = dataLakeAccessor.getFlightData();

        // Process and store the data in the model (modelo)
        processWeatherData(weatherData);
        processFlightData(flightData);
    }

    private void subscribeToEventTopics() {
        // Subscribe to event topics for real-time updates
        flightHandler.subscribe(this::handleFlightEvent);
        weatherHandler.subscribe(this::handleWeatherEvent);
    }

    private void handleFlightEvent(String message) {
        // Process and update the model (modelo) for flight events
        processFlightData(List.of(message));
    }

    private void handleWeatherEvent(String message) {
        // Process and update the model (modelo) for weather events
        processWeatherData(List.of(message));
    }

    private void processFlightData(List<String> flightData) {
        for (String flightEvent : flightData) {
            // Parse flight event and create FlightNode
            FlightNode flightNode = parseFlightEvent(flightEvent);

            // Add flight to the model (modelo)
            modelo.addFlight(flightNode);

            System.out.println("Flight Event Processed: " + flightEvent);
        }
    }

    private void processWeatherData(List<String> weatherData) {
        for (String weatherEvent : weatherData) {
            // Parse weather event and create WeatherNode
            WeatherNode weatherNode = parseWeatherEvent(weatherEvent);

            // Add weather to the model (modelo)
            modelo.addWeather(weatherNode);

            System.out.println("Weather Event Processed: " + weatherEvent);
        }
    }

    private FlightNode parseFlightEvent(String flightEvent) {
        JsonObject flightJson = JsonParser.parseString(flightEvent).getAsJsonObject();

        String flightId = flightJson.get("flightId").getAsString();
        String departureAirport = flightJson.get("departureAirport").getAsString();
        String arrivalAirport = flightJson.get("arrivalAirport").getAsString();
        double price = flightJson.get("price").getAsDouble();

        return new FlightNode(flightId, departureAirport, arrivalAirport, price);
    }

    private WeatherNode parseWeatherEvent(String weatherEvent) {
        JsonObject weatherJson = JsonParser.parseString(weatherEvent).getAsJsonObject();

        String ts = weatherJson.get("ts").getAsString();
        String city = weatherJson.get("city").getAsString();
        double temperature = weatherJson.get("temperature").getAsDouble();
        WeatherType weatherType = parseWeatherType(weatherJson.get("weatherType").getAsString());

        return new WeatherNode(ts, city, temperature, weatherType);
    }

    private WeatherType parseWeatherType(String weatherType) {
        switch (weatherType.toLowerCase()) {
            case "cold":
                return WeatherType.COLD;
            case "warm":
                return WeatherType.WARM;
            case "rainy":
                return WeatherType.RAINY;
            case "clear":
                return WeatherType.CLEAR;
            default:
                throw new IllegalArgumentException("Unknown weather type: " + weatherType);
        }
    }
}
