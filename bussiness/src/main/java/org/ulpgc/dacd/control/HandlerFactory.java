package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Modelo;

import java.util.HashMap;
import java.util.Map;

public class HandlerFactory {
    private Map<String, Constructor<Handler>> handlers = new HashMap<>();
    private Modelo modelo;  // Agregar un campo para el Modelo

    public HandlerFactory(Modelo modelo) {
        this.modelo = modelo;

        // Utilizar un lambda para crear instancias de WeatherHandler con el Modelo
        handlers.put("prediction.Weather", () -> new WeatherHandler(modelo));

        // Utilizar un lambda para crear instancias de HotelHandler con el Modelo
        handlers.put("prediction.Hotel", () -> new HotelHandler(modelo));
    }

    public Handler create(String type) {
        return handlers.get(type).create();
    }

    public interface Constructor<T> {
        T create();
    }
}
