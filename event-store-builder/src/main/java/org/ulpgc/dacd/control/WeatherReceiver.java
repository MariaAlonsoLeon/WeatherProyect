package org.ulpgc.dacd.control;
import org.ulpgc.dacd.model.Weather;

import javax.jms.JMSException;
import java.util.ArrayList;

public interface WeatherReceiver {
    public ArrayList<String> getWeather() throws JMSException;
}