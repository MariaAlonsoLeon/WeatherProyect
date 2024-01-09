package org.ulpgc.dacd.control;

import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {
        if (args.length < 1){
            logger.severe("The locations file path is required as an argument.");
            return;
        }
        HotelOfferSupplier hotelSupplier = new XoteloHotelOfferSupplier("https://data.xotelo.com/api/rates");
        HotelOfferStore hotelStore = new JMSHotelOfferStore("tcp://localhost:61616", "prediction.Hotel", "hotel-provider");
        HotelOfferController weatherController = new HotelOfferController(hotelSupplier, hotelStore, args[0]);
        weatherController.execute();
    }
}
