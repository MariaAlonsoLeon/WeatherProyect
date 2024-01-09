package org.ulpgc.dacd.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.ulpgc.dacd.model.HotelOffer;
import org.ulpgc.dacd.model.Location;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XoteloHotelOfferSupplier implements HotelOfferSupplier {
    private final String apiUrl;
    private static final Logger logger = Logger.getLogger(XoteloHotelOfferSupplier.class.getName());

    public XoteloHotelOfferSupplier(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Override
    public List<HotelOffer> getHotelOffers(Location location, List<String> dates) {
        List<HotelOffer> hotelOffersList = new ArrayList<>();
        for (String currentDate : dates) {
            String url = buildUrl(location.hotelKey(), currentDate);
            System.out.println(url);
            hotelOffersList.addAll(parseHotelOffers(url, location, currentDate));
        }
        return hotelOffersList;
    }

    private String buildUrl(String hotelToken, String currentDate) {
        String tomorrowDate = getCurrentDate();
        return String.format("%s?hotel_key=%s&chk_in=%s&chk_out=%s&currency=EUR", apiUrl, hotelToken, tomorrowDate, currentDate);
    }

    private List<HotelOffer> parseHotelOffers(String url, Location location, String predictionTime) {
        try {
            String jsonData = getHotelOfferFromUrl(url);
            JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
            JsonObject result = jsonObject.getAsJsonObject("result");
            JsonArray rates = result.getAsJsonArray("rates");
            return rates != null ? createHotelOfferList(rates, location, predictionTime) : new ArrayList<>();
        } catch (IOException e) {
            handleException("Error fetching or parsing hotel data", e);
            return new ArrayList<>();
        }
    }

    private List<HotelOffer> createHotelOfferList(JsonArray rates, Location location, String predictionTime) {
        List<HotelOffer> hotelOffersList = new ArrayList<>();
        for (int i = 0; i < rates.size(); i++) {
            JsonObject rate = rates.get(i).getAsJsonObject();
            HotelOffer hotelOffers = createHotelOffer(rate, location, predictionTime);
            if (hotelOffers != null) {
                hotelOffersList.add(hotelOffers);
            }
        }
        return hotelOffersList;
    }

    private HotelOffer createHotelOffer(JsonObject rate, Location location, String predictionTime) {
        try {
            String name = rate.get("name").getAsString();
            float rateValue = rate.get("rate").getAsFloat();
            float tax = rate.has("tax") ? rate.get("tax").getAsFloat() : 0.0f;
            return new HotelOffer(name, rateValue + tax, location, predictionTime);
        } catch (Exception e) {
            handleException("Error creating HotelOffers object", e);
            return null;
        }
    }

    private String getHotelOfferFromUrl(String url) throws IOException {
        Document document = Jsoup.connect(url).ignoreContentType(true).get();
        return document.text();
    }

    private String getCurrentDate() {
        LocalDate tomorrow = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return tomorrow.format(formatter);
    }

    private void handleException(String message, Exception e) {
        logger.log(Level.SEVERE, message, e);
    }


}
