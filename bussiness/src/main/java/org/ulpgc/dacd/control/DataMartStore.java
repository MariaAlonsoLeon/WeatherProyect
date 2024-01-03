package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.HotelOfferNode;
import org.ulpgc.dacd.model.WeatherNode;

public interface DataMartStore {
    public void saveHotelOffer(HotelOfferNode hotelOfferNode);
    public void saveWeather(WeatherNode weatherNode);
}
