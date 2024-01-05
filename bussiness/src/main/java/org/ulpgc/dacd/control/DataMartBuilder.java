package org.ulpgc.dacd.control;

import java.util.List;

public class DataMartBuilder {
    private final DataLakeAccessor dataLakeAccessor;
    private Handler hotelOfferHandler;
    private Handler weatherHandler;

    public DataMartBuilder(DataLakeAccessor dataLakeAccessor, Handler hotelOfferHandler, Handler weatherHandler) {
        this.dataLakeAccessor = dataLakeAccessor;
        this.hotelOfferHandler = hotelOfferHandler;
        this.weatherHandler = weatherHandler;
    }

    public void buildDataMart() {
        List<String> weatherData = dataLakeAccessor.getWeatherData();
        List<String> hotelData = dataLakeAccessor.getHotelData();
        processWeatherData(weatherData);
        processHotelData(hotelData);
    }

    private void processWeatherData(List<String> weatherData) {
        for (String data : weatherData) {
            weatherHandler.handleEvent(data);
        }
    }

    private void processHotelData(List<String> hotelData) {
        for (String data : hotelData) {
            hotelOfferHandler.handleEvent(data);
        }
    }

}
