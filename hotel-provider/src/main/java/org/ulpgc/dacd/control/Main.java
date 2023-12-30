package org.ulpgc.dacd.control;

public class Main {

    public static void main(String[] args) {
        HotelOfferSupplier hotelSupplier = new XoteloHotelOfferSupplier("https://data.xotelo.com/api/rates");
        HotelOfferStore hotelStore = new JMSHotelOfferStore("tcp://localhost:61616", "prediction.Hotel", "hotel-provider");
        HotelOfferController weatherController = new HotelOfferController(hotelSupplier, hotelStore);
        weatherController.execute();
    }
}
