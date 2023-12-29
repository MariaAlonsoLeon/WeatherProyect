package org.ulpgc.dacd.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.ulpgc.dacd.model.HotelTax;
import org.ulpgc.dacd.model.Location;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XoteloHotelTaxSupplier implements HotelTaxSupplier {
    private final String apiUrl;
    private static final Logger logger = Logger.getLogger(XoteloHotelTaxSupplier.class.getName());

    public XoteloHotelTaxSupplier(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Override
    public List<HotelTax> getHotelTaxes(Location location, List<String> dates) {
        List<HotelTax> hotelTaxesList = new ArrayList<>();
        for (String currentDate : dates) {
            String url = buildUrl(location.apiHotelsToken(), currentDate);
            hotelTaxesList.addAll(parseHotelTaxes(url, location, currentDate));
        }
        return hotelTaxesList;
    }

    private String buildUrl(String hotelToken, String currentDate) {
        String tomorrowDate = getTomorrowDate();
        return String.format("%s?hotel_key=%s&chk_in=%s&chk_out=%s&currency=EUR", apiUrl, hotelToken, tomorrowDate, currentDate);
    }

    private List<HotelTax> parseHotelTaxes(String url, Location location, String predictionTime) {
        try {
            String jsonData = getHotelTaxFromUrl(url);
            JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
            JsonObject result = jsonObject.getAsJsonObject("result");
            JsonArray rates = result.getAsJsonArray("rates");
            return rates != null ? createHotelTaxList(rates, location, predictionTime) : new ArrayList<>();
        } catch (IOException e) {
            handleException("Error fetching or parsing hotel data", e);
            return new ArrayList<>();
        }
    }

    private List<HotelTax> createHotelTaxList(JsonArray rates, Location location, String predictionTime) {
        List<HotelTax> hotelTaxesList = new ArrayList<>();
        for (int i = 0; i < rates.size(); i++) {
            JsonObject rate = rates.get(i).getAsJsonObject();
            HotelTax hotelTaxes = createHotel(rate, location, predictionTime);
            if (hotelTaxes != null) {
                hotelTaxesList.add(hotelTaxes);
            }
        }
        return hotelTaxesList;
    }

    private HotelTax createHotel(JsonObject rate, Location location, String predictionTime) {
        try {
            String name = rate.get("name").getAsString();
            float rateValue = rate.get("rate").getAsFloat();
            float tax = rate.has("tax") ? rate.get("tax").getAsFloat() : 0.0f;
            return new HotelTax(name, rateValue + tax, location, predictionTime);
        } catch (Exception e) {
            handleException("Error creating HotelTaxes object", e);
            return null;
        }
    }

    private String getHotelTaxFromUrl(String url) throws IOException {
        Document document = Jsoup.connect(url).ignoreContentType(true).get();
        return document.text();
    }

    private void handleException(String message, Exception e) {
        logger.log(Level.SEVERE, message, e);
    }

    private String getTomorrowDate() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return tomorrow.format(formatter);
    }
}
