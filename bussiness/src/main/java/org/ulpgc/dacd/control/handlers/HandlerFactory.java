package org.ulpgc.dacd.control.handlers;

import org.ulpgc.dacd.control.DataMartStore;
import java.util.HashMap;
import java.util.Map;

public class HandlerFactory {
    private final Map<String, Constructor<Handler>> handlers = new HashMap<>();

    public HandlerFactory(DataMartStore dataMartStore) {
        handlers.put("prediction.Weather", () -> new WeatherHandler(dataMartStore)); // cambiar por WeatherHandler
        handlers.put("prediction.Hotel", () -> new HotelOfferHandler(dataMartStore));
    }

    public Handler create(String type) {
        return handlers.get(type).create();
    }

    public interface Constructor<T> {
        T create();
    }
}
