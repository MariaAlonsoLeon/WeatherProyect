package org.ulpgc.dacd.control;

import java.util.HashMap;
import java.util.Map;

public class HandlerFactory {
    private Map<String, Constructor<Handler>> handlers = new HashMap<>();

    public HandlerFactory() {
        handlers.put("prediction.Weather", () ->(Handler) new WeatherHandler());
        handlers.put("prediction.Flight", () ->(Handler) new FlightHandler());
    }

    public Handler create(String type){
        return handlers.get(type).create();
    }

    public interface Constructor <T>{
        T create();
    }
}
