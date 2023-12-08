package org.ulpgc.dacd.control;

import org.ulpgc.dacd.control.exceptions.StoreException;
import org.ulpgc.dacd.model.Weather;

public interface WeatherStore {
    void save(Weather weather) throws StoreException;
}
