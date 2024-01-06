package org.ulpgc.dacd.control;

import org.ulpgc.dacd.control.exceptions.DataMartStoreException;
import org.ulpgc.dacd.model.HotelOfferNode;
import org.ulpgc.dacd.model.WeatherNode;

public interface DataMartStore {
    void saveHotelOffer(HotelOfferNode hotelOfferNode) throws DataMartStoreException;
    void saveWeather(WeatherNode weatherNode) throws DataMartStoreException;
}
