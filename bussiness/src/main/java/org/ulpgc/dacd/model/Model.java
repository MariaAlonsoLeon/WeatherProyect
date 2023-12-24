package org.ulpgc.dacd.model;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private List<Flight> flights;
    private List<Weather> weathers;

    public Model() {
        this.flights = new ArrayList<>();
        this.weathers = new ArrayList<>();
    }

    public void addFlightData(Flight flight) {
        flights.add(flight);
    }

    public void addWeatherData(Weather weather) {weathers.add(weather);
    }

    public boolean isEmpty() {
        return flights.isEmpty() && weathers.isEmpty();
    }

    // Otros métodos y funcionalidades según sea necesario
}