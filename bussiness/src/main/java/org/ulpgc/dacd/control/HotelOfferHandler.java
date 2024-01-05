package org.ulpgc.dacd.control;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ulpgc.dacd.model.HotelOfferNode;

public class HotelOfferHandler implements Handler {
    private DataMartStore dataMartStore;

    public HotelOfferHandler(DataMartStore dataMartStore) {
        this.dataMartStore = dataMartStore;
    }

    @Override
    public void handleEvent(String eventData) {
        try {
            HotelOfferNode hotelNode = parseHotelEvent(eventData);
            dataMartStore.saveHotelOffer(hotelNode);
            //C:\Users\Maria\Desktop\HotelDB.db
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HotelOfferNode parseHotelEvent(String eventData) {
        ///System.out.println(eventData);
        //System.out.println("hola");
        JsonObject hotelEventJson = parseJson(eventData);
        //String companyName = hotelEventJson.get("name").getAsString();
        //System.out.println(hotelEventJson.has("rate"));
        double tax = hotelEventJson.get("rate").getAsDouble();
        JsonObject locationJson = hotelEventJson.getAsJsonObject("location");
        String locationName = locationJson.get("name").getAsString();
        String hotelName = locationJson.get("hotelName").getAsString();
        String predictionTime = hotelEventJson.get("predictionTime").getAsString();
        String companyName = hotelEventJson.get("companyName").getAsString();
        return new HotelOfferNode(hotelName ,tax, locationName, companyName, predictionTime);
    }

    private JsonObject parseJson(String jsonData) {
        try {
            JsonParser parser = new JsonParser();
            return parser.parse(jsonData).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new JsonObject();
        }
    }
}