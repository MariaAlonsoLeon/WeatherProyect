package org.ulpgc.dacd.control.handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ulpgc.dacd.control.DataMartStore;
import org.ulpgc.dacd.model.HotelOfferRecord;

public class HotelOfferHandler implements Handler {
    private DataMartStore dataMartStore;

    public HotelOfferHandler(DataMartStore dataMartStore) {
        this.dataMartStore = dataMartStore;
    }

    @Override
    public void handleEvent(String message) {
        try {
            HotelOfferRecord hotelNode = parseHotelOfferEvent(message);
            dataMartStore.saveHotelOffer(hotelNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HotelOfferRecord parseHotelOfferEvent(String message) {
        JsonObject hotelEventJson = parseJson(message);
        double tax = hotelEventJson.get("rate").getAsDouble();
        JsonObject locationJson = hotelEventJson.getAsJsonObject("location");
        String locationName = locationJson.get("name").getAsString();
        String hotelName = locationJson.get("hotelName").getAsString();
        String predictionTime = hotelEventJson.get("predictionTime").getAsString();
        String companyName = hotelEventJson.get("companyName").getAsString();
        return new HotelOfferRecord(hotelName ,tax, locationName, companyName, predictionTime);
    }

    private JsonObject parseJson(String message) {
        try {
            JsonParser parser = new JsonParser();
            return parser.parse(message).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new JsonObject();
        }
    }
}