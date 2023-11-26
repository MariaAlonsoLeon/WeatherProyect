package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Weather;

import java.util.ArrayList;

public class EventController {
    private final Listener listener;
    private final SQLiteWeatherStore SQLWeatherStore;

    public EventController(Listener listener, SQLiteWeatherStore SQLWeatherStore) {
        this.listener = listener;
        this.SQLWeatherStore = SQLWeatherStore;
    }

    public void execute(){
        ArrayList<Weather> weathers = this.listener.getWeather();
        this.SQLWeatherStore.save(weathers);
    }
}