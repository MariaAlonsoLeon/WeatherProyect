package org.ulpgc.dacd.model;

import java.time.Instant;

public class Weather {
    private Instant timestamp;
    private String condition;

    // TODO poner otros atributos

    public Weather(Instant timestamp, String condition) {
        this.timestamp = timestamp; //Necesario para un datamart?
        this.condition = condition;
    }

}
