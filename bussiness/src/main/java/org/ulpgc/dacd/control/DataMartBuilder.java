package org.ulpgc.dacd.control;

import java.util.List;

import org.ulpgc.dacd.model.Modelo;

public class DataMartBuilder {
    private final Modelo modelo;
    private final DataLakeAccessor dataLakeAccessor;

    public DataMartBuilder(Modelo modelo, DataLakeAccessor dataLakeAccessor) {
        this.modelo = modelo;
        this.dataLakeAccessor = dataLakeAccessor;
    }

    public void buildDataMart() {
        List<String> weatherData = dataLakeAccessor.getWeatherData();
        List<String> hotelData = dataLakeAccessor.getHotelData();

        processWeatherData(weatherData);
        processHotelData(hotelData);
    }

    private void processWeatherData(List<String> weatherData) {
        for (String data : weatherData) {
            WeatherHandler weatherHandler = new WeatherHandler(modelo);
            weatherHandler.handleEvent(data);
        }
    }

    private void processHotelData(List<String> hotelData) {
        for (String data : hotelData) {
            HotelOfferHandler hotelHandler = new HotelOfferHandler(modelo);
            hotelHandler.handleEvent(data);
        }
    }

}
