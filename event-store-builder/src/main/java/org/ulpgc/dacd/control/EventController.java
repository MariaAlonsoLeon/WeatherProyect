package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Weather;

import javax.jms.JMSException;
import java.util.ArrayList;

public class EventController {
    private final WeatherReceiver weatherReceiver;
    private final WeatherStore weatherStore;

    public EventController(WeatherReceiver weatherReceiver, WeatherStore weatherStore) {
        this.weatherReceiver = weatherReceiver;
        this.weatherStore = weatherStore;
    }

    public void execute() throws JMSException {
        ArrayList<String> weathers = this.weatherReceiver.getWeather();

        if (weathers != null) {
            for (String weather : weathers) {
                weatherStore.save(weather);
            }
        }
    }
}