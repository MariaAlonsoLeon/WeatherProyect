package org.ulpgc.dacd.control;

import org.ulpgc.dacd.control.exceptions.WeatherReceiverException;
import java.util.ArrayList;

public interface WeatherReceiver {
    ArrayList<String> getWeather() throws WeatherReceiverException;
}