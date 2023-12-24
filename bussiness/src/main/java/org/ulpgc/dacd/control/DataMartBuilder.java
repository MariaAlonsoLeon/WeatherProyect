package org.ulpgc.dacd.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DataMartBuilder {
    private DataLakeAccessor dataLakeAccessor;
    private FlightHandler flightHandler;
    private WeatherHandler weatherHandler;

    private Map<String, String> dataMart; // Assuming a simple map structure for the in-memory graph

    public DataMartBuilder(DataLakeAccessor dataLakeAccessor, FlightHandler flightHandler, WeatherHandler weatherHandler) {
        this.dataLakeAccessor = dataLakeAccessor;
        this.flightHandler = flightHandler;
        this.weatherHandler = weatherHandler;
        this.dataMart = new HashMap<>();
    }

    public void buildDataMart() {
        loadDataFromDataLake();
        subscribeToEventTopics();
    }

    private void loadDataFromDataLake() {
        // Load initial data from DataLake
        List<String> weatherData = dataLakeAccessor.getWeatherData();
        List<String> flightData = dataLakeAccessor.getFlightData();

        // Process and store the data in the in-memory graph (dataMart)
        processWeatherData(weatherData);
        processFlightData(flightData);
    }

    private void subscribeToEventTopics() {
        // Subscribe to event topics for real-time updates
        flightHandler.subscribe(this::handleFlightEvent);
        weatherHandler.subscribe(this::handleWeatherEvent);
    }

    private void handleFlightEvent(String message) {
        // Process and update the in-memory graph (dataMart) for flight events
        processFlightData(List.of(message));
    }

    private void handleWeatherEvent(String message) {
        // Process and update the in-memory graph (dataMart) for weather events
        processWeatherData(List.of(message));
    }

    private void processFlightData(List<String> flightData) {
        for (String flightEvent : flightData) {
            JsonObject eventJson = JsonParser.parseString(flightEvent).getAsJsonObject();

            // Extract relevant information from the flight event JSON
            String flightId = eventJson.get("flightId").getAsString();
            String departureAirport = eventJson.get("departureAirport").getAsString();
            String arrivalAirport = eventJson.get("arrivalAirport").getAsString();
            double price = eventJson.get("price").getAsDouble();

            // Update dataMart with flightData
            String key = flightId; // You might want to use a unique key
            String value = "Flight from " + departureAirport + " to " + arrivalAirport + " - Price: " + price;
            dataMart.put(key, value);

            System.out.println("Flight Event Processed: " + flightEvent);
        }
    }

    private void processWeatherData(List<String> weatherData) {
        for (String weatherEvent : weatherData) {
            JsonObject eventJson = JsonParser.parseString(weatherEvent).getAsJsonObject();

            // Extract relevant information from the weather event JSON
            String ts = eventJson.get("ts").getAsString();
            double temperature = eventJson.get("temperature").getAsDouble();

            // Update dataMart with weatherData
            String key = ts; // You might want to use a unique key
            String value = "Weather in " + ts + ", Temperature: " + temperature;
            dataMart.put(key, value);

            System.out.println("Weather Event Processed: " + weatherEvent);
        }
    }
}
