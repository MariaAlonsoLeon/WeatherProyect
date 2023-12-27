package org.ulpgc.dacd.control;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.ulpgc.dacd.model.HotelPriceNode;
import org.ulpgc.dacd.model.Modelo;
import org.ulpgc.dacd.model.WeatherNode;
import org.ulpgc.dacd.model.WeatherType;
import org.ulpgc.dacd.model.LocationNode;

import java.util.List;

public class DataMartBuilder {
    private final Modelo modelo;
    private final DataLakeAccessor dataLakeAccessor;

    public DataMartBuilder(Modelo modelo, DataLakeAccessor dataLakeAccessor) {
        this.modelo = modelo;
        this.dataLakeAccessor = dataLakeAccessor;
    }

    public void buildDataMart() {
        // Obtener los últimos datos del DataLake
        List<String> weatherData = dataLakeAccessor.getWeatherData();
        List<String> hotelData = dataLakeAccessor.getHotelData();

        // Procesar y actualizar el Datamart con los datos obtenidos
        processWeatherData(weatherData);
        processHotelData(hotelData);
    }

    private void processWeatherData(List<String> weatherData) {
        // Procesar y actualizar el Datamart con los datos meteorológicos
        for (String data : weatherData) {
            WeatherHandler weatherHandler = new WeatherHandler(modelo);
            weatherHandler.handleEvent(data);
        }
    }

    private void processHotelData(List<String> hotelData) {
        // Procesar y actualizar el Datamart con los datos de hoteles
        for (String data : hotelData) {
            HotelHandler hotelHandler = new HotelHandler(modelo);
            hotelHandler.handleEvent(data);
        }
    }

    // Otras funciones según sea necesario para actualizar o consultar el Datamart
}
