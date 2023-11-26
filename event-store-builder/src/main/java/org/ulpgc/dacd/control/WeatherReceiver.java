package org.ulpgc.dacd.control;
import org.ulpgc.dacd.model.Weather;
import java.util.ArrayList;

public interface WeatherReceiver {
    public ArrayList<Weather> getWeather();
}