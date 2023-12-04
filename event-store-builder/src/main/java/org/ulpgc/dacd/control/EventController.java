package org.ulpgc.dacd.control;

import org.ulpgc.dacd.control.exceptions.WeatherReceiverException;

import javax.jms.JMSException;
import java.util.ArrayList;

public class EventController {
    private final WeatherReceiver weatherReceiver;
    private final WeatherStore weatherStore;
    private static final int EVENTS_PER_BATCH = 40;

    public EventController(WeatherReceiver weatherReceiver, WeatherStore weatherStore) {
        this.weatherReceiver = weatherReceiver;
        this.weatherStore = weatherStore;
    }

    public void execute() throws WeatherReceiverException, JMSException {
        while (true) {
            System.out.println("Hola");
            ArrayList<String> weathers = this.weatherReceiver.getWeather();

            if (weathers != null) {
                weatherStore.save(weathers);
            }

            try {
                Thread.sleep(1 * 60 * 1000); // Espera 6 horas antes de la próxima ejecución
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
