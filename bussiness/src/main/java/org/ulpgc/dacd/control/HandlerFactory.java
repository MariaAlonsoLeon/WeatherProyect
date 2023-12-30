package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Modelo;

import java.util.HashMap;
import java.util.Map;

public class HandlerFactory {
    private Map<String, Constructor<Handler>> handlers = new HashMap<>();
    private Modelo modelo;

    public HandlerFactory(Modelo modelo) {
        this.modelo = modelo;
        handlers.put("prediction.Weather", () -> new WeatherHandler(modelo));
        handlers.put("prediction.Hotel", () -> new HotelOfferHandler(modelo));
    }

    public Handler create(String type) {
        return handlers.get(type).create();
    }

    public interface Constructor<T> {
        T create();
    }
}
