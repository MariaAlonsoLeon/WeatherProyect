package org.ulpgc.dacd.control;
import org.ulpgc.dacd.control.exceptions.WeatherReceiverException;

import javax.jms.JMSException;
import java.util.ArrayList;

public class EventController {
    private final WeatherReceiver weatherReceiver;
    private final WeatherStore weatherStore;

    public EventController(WeatherReceiver weatherReceiver, WeatherStore weatherStore) {
        this.weatherReceiver = weatherReceiver;
        this.weatherStore = weatherStore;
    }

    public void execute() throws WeatherReceiverException {
        ArrayList<String> weathers = this.weatherReceiver.getWeather();
        if (weathers != null) {
            weatherStore.save(weathers);
        }
    }
}