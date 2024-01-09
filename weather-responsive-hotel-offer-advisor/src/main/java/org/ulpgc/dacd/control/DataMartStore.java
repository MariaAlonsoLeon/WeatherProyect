package org.ulpgc.dacd.control;

import org.ulpgc.dacd.control.exceptions.DataMartStoreException;
import org.ulpgc.dacd.model.HotelOfferRecord;
import org.ulpgc.dacd.model.WeatherRecord;

public interface DataMartStore {
    void saveHotelOffer(HotelOfferRecord hotelOfferRecord) throws DataMartStoreException;
    void saveWeather(WeatherRecord weatherRecord) throws DataMartStoreException;
}
