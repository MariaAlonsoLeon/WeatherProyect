package org.ulpgc.dacd.control;

import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        HotelSupplier hotelSupplier = new XoteloHotelSupplier("https://data.xotelo.com/api/rates");
        HotelStore hotelStore = new JMSHotelStore("tcp://localhost:61616", "prediction.Hotel", "PredictionProvider");
        HotelPricesController weatherController = new HotelPricesController(hotelSupplier, hotelStore);
        weatherController.execute();
    }
}
