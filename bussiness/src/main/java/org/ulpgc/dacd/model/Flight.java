package org.ulpgc.dacd.model;

import java.time.Instant;

public class Flight {
    private Instant departureTime;
    private String airline;
    // Otros atributos relevantes para Flight

    public Flight(Instant departureTime, String airline) {
        this.departureTime = departureTime;
        this.airline = airline;
        // Inicializar otros atributos según sea necesario
    }

    // Getters y setters según sea necesario
}