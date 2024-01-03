package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Modelo;

import java.util.HashMap;
import java.util.Map;

public class HandlerFactory {
    private Map<String, Constructor<Handler>> handlers = new HashMap<>();


    public HandlerFactory(DataMartStore dataMartStore) {
        handlers.put("prediction.Weather", () -> new WeatherHandler(dataMartStore));
        handlers.put("prediction.Hotel", () -> new HotelOfferHandler(dataMartStore));
    }

    public Handler create(String type) {
        return handlers.get(type).create();
    }

    public interface Constructor<T> {
        T create();
    }
}
