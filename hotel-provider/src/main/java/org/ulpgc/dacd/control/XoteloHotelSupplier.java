package org.ulpgc.dacd.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.ulpgc.dacd.model.HotelTaxes;
import org.ulpgc.dacd.model.Location;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XoteloHotelSupplier implements HotelSupplier {
    private final String apiUrl;
    private static final Logger logger = Logger.getLogger(XoteloHotelSupplier.class.getName());

    public XoteloHotelSupplier(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Override
    public List<HotelTaxes> getHotelPrices(Location location) {
        List<HotelTaxes> hotelTaxesList = new ArrayList<>();
        for (String currentDate : generateDateList()) {
            System.out.println(currentDate);
            String url = buildUrl(location.apiHotelsToken(), currentDate);
            System.out.println(url);
            hotelTaxesList.addAll(parseHotelData(url, location));
        }

        return hotelTaxesList;
    }

    private String buildUrl(String hotelToken, String currentDate) {
        String tomorrowDate = getTomorrowDate();
        return String.format("%s?hotel_key=%s&chk_in=%s&chk_out=%s", apiUrl, hotelToken, tomorrowDate, currentDate);
    }

    private List<HotelTaxes> parseHotelData(String url, Location location) {
        try {
            String jsonData = getHotelFromUrl(url);
            JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
            JsonObject result = jsonObject.getAsJsonObject("result");
            JsonArray rates = result.getAsJsonArray("rates");
            return rates != null ? createHotelList(rates, location) : new ArrayList<>();
        } catch (IOException e) {
            handleException("Error fetching or parsing hotel data", e);
            return new ArrayList<>();
        }
    }

    private List<HotelTaxes> createHotelList(JsonArray rates, Location location) {
        List<HotelTaxes> hotelTaxesList = new ArrayList<>();
        for (int i = 0; i < rates.size(); i++) {
            JsonObject rate = rates.get(i).getAsJsonObject();
            HotelTaxes hotelTaxes = createHotel(rate, location);
            if (hotelTaxes != null) {
                hotelTaxesList.add(hotelTaxes);
            }
        }
        return hotelTaxesList;
    }

    private HotelTaxes createHotel(JsonObject rate, Location location) {
        try {
            String name = rate.get("name").getAsString();
            float rateValue = rate.get("rate").getAsFloat();
            return new HotelTaxes(name, rateValue, location);
        } catch (Exception e) {
            handleException("Error creating HotelTaxes object", e);
            return null;
        }
    }

    private String getHotelFromUrl(String url) throws IOException {
        Document document = Jsoup.connect(url).ignoreContentType(true).get();
        return document.text();
    }

    private void handleException(String message, Exception e) {
        logger.log(Level.SEVERE, message, e);
    }

    private List<String> generateDateList() {
        List<String> dateList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate tomorrow = LocalDate.now().plusDays(2);

        for (int i = 0; i < 5; i++) {
            dateList.add(tomorrow.plusDays(i).format(formatter));
        }

        return dateList;
    }

    private String getTomorrowDate() { // TODO change this in order to not repeat code
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return tomorrow.format(formatter);
    }
}
