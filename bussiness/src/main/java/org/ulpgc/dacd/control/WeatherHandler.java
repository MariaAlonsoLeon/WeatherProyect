package org.ulpgc.dacd.control;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ulpgc.dacd.model.Modelo;
import org.ulpgc.dacd.model.WeatherNode;
import org.ulpgc.dacd.model.WeatherType;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WeatherHandler implements Handler {
    private final Modelo modelo;
    private final LocationRecommendationService locationRecommendationService;

    public WeatherHandler(Modelo modelo) {
        this.modelo = modelo;
        this.locationRecommendationService = new LocationRecommendationService();
    }

    @Override
    public void handleEvent(String eventData) {
        try {
            // Parsear el evento JSON del clima
            WeatherNode weatherNode = parseWeatherEvent(eventData);

            // Actualizar el modelo con los datos del clima
            updateModelWithWeatherNode(weatherNode);
        } catch (Exception e) {
            // Manejar errores de parsing o actualización del modelo
            // Puedes registrar el error o tomar medidas específicas según tus necesidades
            e.printStackTrace();
        }
    }

    private WeatherNode parseWeatherEvent(String eventData) {
        // Implementar la lógica de parsing del evento JSON del clima
        JsonObject weatherEventJson = parseJson(eventData);

        String predictionTime = weatherEventJson.get("predictionTime").getAsString();
        JsonObject locationJson = weatherEventJson.getAsJsonObject("location");
        String locationName = locationJson.get("name").getAsString();
        int humidity = weatherEventJson.get("humidity").getAsInt();
        double temperature = weatherEventJson.get("temperature").getAsDouble();
        int clouds = weatherEventJson.get("clouds").getAsInt();
        float rain = weatherEventJson.get("rain").getAsFloat();

        // Determine the weather type using the LocationRecommendationService
        WeatherType weatherType = locationRecommendationService.determineWeatherType(temperature, rain, clouds);

        // Crear una instancia de WeatherNode con los datos extraídos
        return new WeatherNode(predictionTime, locationName, humidity, temperature, clouds, rain, weatherType);
    }

    private void updateModelWithWeatherNode(WeatherNode weatherNode) {
        // Actualizar el modelo con los datos del clima
        // Puedes llamar a métodos específicos del modelo según tu implementación
        modelo.updateWeatherNode(weatherNode);
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