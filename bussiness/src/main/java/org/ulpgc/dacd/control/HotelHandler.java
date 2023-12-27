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
            // Parsear el evento JSON del hotel
            HotelPriceNode hotelNode = parseHotelEvent(eventData);

            // Actualizar el modelo con los datos del hotel
            updateModelWithHotelNode(hotelNode);
        } catch (Exception e) {
            // Manejar errores de parsing o actualización del modelo
            // Puedes registrar el error o tomar medidas específicas según tus necesidades
            e.printStackTrace();
        }
    }

    private HotelPriceNode parseHotelEvent(String eventData) {
        // Implementar la lógica de parsing del evento JSON del hotel
        JsonObject hotelEventJson = parseJson(eventData);
        String companyName = hotelEventJson.get("name").getAsString();
        double tax = hotelEventJson.get("tax").getAsDouble();
        JsonObject locationJson = hotelEventJson.getAsJsonObject("location");
        String locationName = locationJson.get("name").getAsString();

        // Crear una instancia de HotelNode con los datos extraídos
        return new HotelPriceNode(companyName, tax, locationName);
    }

    private void updateModelWithHotelNode(HotelPriceNode hotelNode) {
        // Actualizar el modelo con los datos del hotel
        // Puedes llamar a métodos específicos del modelo según tu implementación
        modelo.updateHotelNode(
                hotelNode.name(),
                hotelNode.tax(),
                hotelNode.locationName()
        );
    }

    // Método ficticio para el parsing JSON
    private JsonObject parseJson(String jsonData) {
        try {
            // Utilizar Gson para parsear el JSON a un objeto JsonObject
            JsonParser parser = new JsonParser();
            return parser.parse(jsonData).getAsJsonObject();
        } catch (Exception e) {
            // Manejar errores de parsing
            e.printStackTrace();
            return new JsonObject(); // Devolver un JsonObject vacío en caso de error
        }
    }
}