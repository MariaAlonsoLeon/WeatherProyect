package org.ulpgc.dacd.control;

public class Main {

    public static void main(String[] args) {
        HotelTaxSupplier hotelSupplier = new XoteloHotelTaxSupplier("https://data.xotelo.com/api/rates");
        HotelTaxStore hotelStore = new JMSHotelTaxStore("tcp://localhost:61616", "prediction.Hotel", "PredictionProvider");
        HotelTaxController weatherController = new HotelTaxController(hotelSupplier, hotelStore);
        weatherController.execute();
    }
}
