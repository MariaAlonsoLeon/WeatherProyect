package org.ulpgc.dacd.model;

import java.util.HashMap;
import java.util.Map;

public class Modelo {
    private Map<String, FlightNode> vuelos;
    private Map<String, WeatherNode> climas;
    private Map<String, LocationNode> localizaciones;

    public Modelo() {
        this.vuelos = new HashMap<>();
        this.climas = new HashMap<>();
        this.localizaciones = new HashMap<>();
    }

    public void addFlight(FlightNode vuelo) {
        vuelos.put(vuelo.id(), vuelo);
    }

    public void addWeather(WeatherNode clima) {
        climas.put(clima.id(), clima);
    }

    public void addLocation(LocationNode localizacion) {
        localizaciones.put(localizacion.id(), localizacion);
    }

    public FlightNode getFlight(String id) {
        return vuelos.get(id);
    }

    public WeatherNode getWeather(String id) {
        return climas.get(id);
    }

    public LocationNode getLocation(String id) {
        return localizaciones.get(id);
    }

}