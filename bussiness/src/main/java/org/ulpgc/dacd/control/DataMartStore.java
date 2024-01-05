package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.HotelOfferNode;
import org.ulpgc.dacd.model.WeatherNode;

public interface DataMartStore {
    void saveHotelOffer(HotelOfferNode hotelOfferNode);
    void saveWeather(WeatherNode weatherNode);
}
