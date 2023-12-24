package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Flight;
import org.ulpgc.dacd.model.Model;
import org.ulpgc.dacd.model.Weather;

import org.ulpgc.dacd.model.Flight;
import org.ulpgc.dacd.model.Model;
import org.ulpgc.dacd.model.Weather;

public class DataMartBuilder {
    private final DataLakeAccessor dataLakeAccessor;
    private final FlightHandler flightHandler;
    private final WeatherHandler weatherHandler;
    private final Model dataMart;

    public DataMartBuilder(DataLakeAccessor dataLakeAccessor, FlightHandler flightHandler, WeatherHandler weatherHandler) {
        this.dataLakeAccessor = dataLakeAccessor;
        this.flightHandler = flightHandler;
        this.weatherHandler = weatherHandler;
        this.dataMart = new Model();
    }

    /*public Model buildDataMart() {
        if (dataMart.isEmpty()) {
            loadInitialDataFromDataLake();
        } else {
            loadCurrentData();
        }
        return dataMart;
    }*/

    /*private void loadInitialDataFromDataLake() {
        Flight flightData = flightHandler.createFlight(dataLakeAccessor.getFlightData());
        dataMart.addFlightData(flightData);
        Weather weatherData = weatherHandler.createWeather(dataLakeAccessor.getWeatherData());
        dataMart.addWeatherData(weatherData);
    }

    private void loadCurrentData() {
        flightHandler.handleEvent(""); // Ajusta el método según la implementación real.
        weatherHandler.handleEvent(""); // Ajusta el método según la implementación real.
    }

    public void destroyDataMart() {
        dataMart.clearData();
    }*/
}