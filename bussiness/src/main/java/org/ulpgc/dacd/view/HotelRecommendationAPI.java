package org.ulpgc.dacd.view;

import com.google.gson.Gson;
import org.ulpgc.dacd.control.DataMartConsultant;
import org.ulpgc.dacd.control.exceptions.DataMartException;
import org.ulpgc.dacd.view.model.HotelOffer;
import org.ulpgc.dacd.view.model.Weather;
import spark.Request;
import spark.Response;
import org.ulpgc.dacd.view.model.Offer;
import java.util.Map;
import static spark.Spark.*;

public class HotelRecommendationAPI {
    private final DataMartConsultant dataMartConsultant;

    public HotelRecommendationAPI(DataMartConsultant dataMartConsultant) {
        this.dataMartConsultant = dataMartConsultant;
    }

    public void init() {
        configureStaticFiles();
        setupLocationsEndpoint();
        setupCheapestOfferEndpoint();
        setupCheapestOffersByWeatherAndDateEndpoint();
    }

    private void configureStaticFiles() {
        staticFiles.location("/public");
    }

    private void setupLocationsEndpoint() {
        get("/locations", this::getLocations);
    }

    private String getLocations(Request request, Response response) throws DataMartException {
        response.type("application/json");
        String weatherType = request.queryParams("weather");
        String date = request.queryParams("date");
        Map<String, Weather> locations = dataMartConsultant.getLocationsByWeatherAndDate(weatherType, date);
        System.out.println(locations);
        return new Gson().toJson(locations);
    }

    private void setupCheapestOfferEndpoint() {
        get("/offer/:location/:date", this::getCheapestOffer);
    }

    private String getCheapestOffer(Request request, Response response) throws DataMartException {
        response.type("application/json");
        String location = request.params(":location");
        String bookingDate = request.params(":date");
        HotelOffer cheapestOffer = dataMartConsultant.getCheapestOffer(location, bookingDate);
        return new Gson().toJson(cheapestOffer);
    }

    private void setupCheapestOffersByWeatherAndDateEndpoint() {
        get("/cheapest-offers", this::getCheapestOffersByWeatherAndDate);
    }

    private String getCheapestOffersByWeatherAndDate(Request request, Response response) throws DataMartException {
        response.type("application/json");
        String weatherType = request.queryParams("weather");
        String date = request.queryParams("date");
        Offer cheapestOffers = dataMartConsultant.getCheapestHotelOffersByWeatherAndDate(weatherType, date);
        return new Gson().toJson(cheapestOffers);
    }
}
