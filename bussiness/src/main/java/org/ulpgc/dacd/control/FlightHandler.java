package org.ulpgc.dacd.control;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class FlightHandler implements Handler {
    private List<Consumer<String>> subscribers = new ArrayList<>();

    @Override
    public void handleEvent(String message) {
        // Process flight event
        JsonObject flightEventJson = JsonParser.parseString(message).getAsJsonObject();

        // Extract relevant information from the flight event JSON
        String flightId = flightEventJson.get("flightId").getAsString();
        String departureAirport = flightEventJson.get("departureAirport").getAsString();
        String arrivalAirport = flightEventJson.get("arrivalAirport").getAsString();
        double price = flightEventJson.get("price").getAsDouble();

        // TODO change the example for a real process

        // Notify subscribers with the processed information
        notifySubscribers("Flight ID: " + flightId +
                ", Departure: " + departureAirport + ", Arrival: " + arrivalAirport +
                ", Price: " + price);
    }

    public void subscribe(Consumer<String> subscriber) {
        subscribers.add(subscriber);
    }

    private void notifySubscribers(String message) {
        subscribers.forEach(subscriber -> subscriber.accept(message));
    }
}
