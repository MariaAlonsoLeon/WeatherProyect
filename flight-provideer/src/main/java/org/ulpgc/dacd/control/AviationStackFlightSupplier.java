package org.ulpgc.dacd.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ulpgc.dacd.model.Flight;
import org.ulpgc.dacd.model.Location;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AviationStackFlightSupplier implements FlightSupplier {
    private final String accessKey;
    private final String templateUrl;
    private static final Logger logger = Logger.getLogger(AviationStackFlightSupplier.class.getName());

    public AviationStackFlightSupplier(String accessKey, String templateUrl) {
        this.accessKey = accessKey;
        this.templateUrl = templateUrl;
    }

    @Override
    public List<Flight> getFlights(Location departure, Location arrival) {
        //String url = buildUrl(departure, arrival);
        List<Flight> flights = new ArrayList<>();
        for (String currentDate : generateDateList()) {
            String url = buildUrl(departure, arrival, currentDate);
            System.out.println(url);
            flights.addAll(parseFlightData(url, departure, arrival));
        }
        return flights;
    }

    private String buildUrl(Location departure, Location arrival, String currentDate) {
        String departureCode = departure.name(); // Use IATA code or ICAO code based on your preference
        String arrivalCode = arrival.name(); // Use IATA code or ICAO code based on your preference

        String params = String.format(
                "access_key=%s&limit=1&flight_date=%s&dep_iata=%s&arr_iata=%s",
                accessKey, currentDate,departureCode, arrivalCode
        );
        return String.format("%s?%s", templateUrl, params);
    }

    private List<Flight> parseFlightData(String url, Location departure, Location arrival) {
        try {
            String jsonFlights = getFlightsFromUrl(url);
            JsonObject jsonObject = JsonParser.parseString(jsonFlights).getAsJsonObject();
            JsonArray data = jsonObject.getAsJsonArray("data");
            return data != null ? extractFlights(data, departure, arrival) : new ArrayList<>();
        } catch (IOException e) {
            handleException("Error fetching or parsing flight data", e);
            return new ArrayList<>();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Flight> extractFlights(JsonArray data, Location departure, Location arrival) {
        List<Flight> flights = new ArrayList<>();
        for (JsonElement element : data) {
            if (element.isJsonObject()) {
                Flight flight = createFlightFromData(element.getAsJsonObject(), departure, arrival);
                if (flight != null) {
                    flights.add(flight);
                }
            }
        }
        return flights;
    }

    private Flight createFlightFromData(JsonObject flightData, Location departure, Location arrival) {
        try {
            String flightDateString = flightData.get("flight_date").getAsString();
            String flightStatus = flightData.get("flight_status").getAsString();
            String airlineIata = flightData.getAsJsonObject("airline").get("name").getAsString();

            // Verificar si flightNumber es nulo
            String flightNumber = flightData.getAsJsonObject("flight").get("number").isJsonNull()
                    ? "Not assigned yet"
                    : flightData.getAsJsonObject("flight").get("number").getAsString();

            System.out.println("prueba");
            Flight flight = new Flight(flightDateString, flightStatus, departure, arrival, airlineIata, flightNumber);
            System.out.println(flight);

            return flight;
        } catch (Exception e) {
            handleException("Error creating Flight object", e);
            return null;
        }
    }

    private Location parseLocation(JsonObject locationData) {
        String name = locationData.get("airport").getAsString();
        double lat = locationData.getAsJsonObject("location").get("latitude").getAsDouble();
        double lon = locationData.getAsJsonObject("location").get("longitude").getAsDouble();
        return new Location(name, lat, lon);
    }

    private String getFlightsFromUrl(String url) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private void handleException(String message, Exception e) {
        logger.log(Level.SEVERE, message, e);
    }

    private List<String> generateDateList() {
        List<String> dateList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.now();
        for (int i = 0; i <= 5; i++) {
            dateList.add(currentDate.plusDays(i).format(formatter));
        }

        return dateList;
    }
}

