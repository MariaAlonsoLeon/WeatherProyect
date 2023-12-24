package org.ulpgc.dacd.control;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ulpgc.dacd.model.Flight;
import org.ulpgc.dacd.model.Model;
import org.ulpgc.dacd.model.Weather;

import java.time.Instant;

import com.google.gson.Gson;

public class FlightHandler implements Handler {
    private final Gson gson = new Gson();

    @Override
    public void handleEvent(String message) {
        Flight flight = extractFlightInfo(message);
        System.out.println("Handling flight event: " + flight);
    }

    private Flight extractFlightInfo(String message) {
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
        Instant departureAirport = Instant.parse(jsonObject.get("departureTime").getAsString());
        String airline = jsonObject.get("airline").getAsString();

        return new Flight(departureAirport, airline);
    }
}