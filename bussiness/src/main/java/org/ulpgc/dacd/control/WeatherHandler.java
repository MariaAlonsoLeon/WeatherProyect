package org.ulpgc.dacd.control;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WeatherHandler implements Handler {
    private List<Consumer<String>> subscribers = new ArrayList<>();

    @Override
    public void handleEvent(String message) {
        // Process weather event
        JsonObject weatherEventJson = JsonParser.parseString(message).getAsJsonObject();

        // Extract relevant information from the weather event JSON
        String city = weatherEventJson.get("city").getAsString();
        String condition = weatherEventJson.get("condition").getAsString();
        double temperature = weatherEventJson.get("temperature").getAsDouble();

        // TODO change the example for a real process
        // Example: Perform some processing based on the weather event
        System.out.println("Weather Event Processed: City - " + city + ", Condition - " + condition + ", Temperature - " + temperature);

        // Notify subscribers with the processed information
        notifySubscribers(city + " - Condition: " + condition + ", Temperature: " + temperature);
    }

    public void subscribe(Consumer<String> subscriber) {
        subscribers.add(subscriber);
    }

    private void notifySubscribers(String message) {
        subscribers.forEach(subscriber -> subscriber.accept(message));
    }
}
