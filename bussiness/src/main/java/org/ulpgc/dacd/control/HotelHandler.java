package org.ulpgc.dacd.control;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ulpgc.dacd.model.HotelPriceNode;
import org.ulpgc.dacd.model.Modelo;

public class HotelHandler implements Handler {
    private final Modelo modelo;

    public HotelHandler(Modelo modelo) {
        this.modelo = modelo;
    }

    @Override
    public void handleEvent(String eventData) {
        try {
            HotelPriceNode hotelNode = parseHotelEvent(eventData);
            updateModelWithHotelNode(hotelNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HotelPriceNode parseHotelEvent(String eventData) {
        JsonObject hotelEventJson = parseJson(eventData);
        String companyName = hotelEventJson.get("name").getAsString();
        double tax = hotelEventJson.get("tax").getAsDouble();
        JsonObject locationJson = hotelEventJson.getAsJsonObject("location");
        String locationName = locationJson.get("name").getAsString();
        return new HotelPriceNode(companyName, tax, locationName);
    }

    private void updateModelWithHotelNode(HotelPriceNode hotelNode) {
        modelo.updateHotelNode(
                hotelNode.name(),
                hotelNode.tax(),
                hotelNode.locationName()
        );
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