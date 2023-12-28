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
        String hotelName = locationJson.get("hotelName").getAsString();
        String predictionTime = hotelEventJson.get("predictionTime").getAsString();

        // Asegurarse de que la asignación de ubicación se realice correctamente
        System.out.println(locationName);
        return new HotelPriceNode(hotelName, tax, locationName, predictionTime);
    }


    private void updateModelWithHotelNode(HotelPriceNode hotelNode) {
        modelo.updateHotelNode(hotelNode);
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