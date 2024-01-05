package org.ulpgc.dacd.view;

import com.google.gson.Gson;
import org.ulpgc.dacd.control.DataMartConsultant;
import org.ulpgc.dacd.view.model.HotelOffer;
import org.ulpgc.dacd.view.model.Weather;
import spark.Request;
import spark.Response;
import org.ulpgc.dacd.view.model.Offer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static spark.Spark.*;

public class HotelRecommendationAPI {
    private final DataMartConsultant dataMartConsultant;

    public HotelRecommendationAPI(DataMartConsultant dataMartConsultant) {
        this.dataMartConsultant = dataMartConsultant;
    }

    public void init() {
        configureStaticFiles();
        setupClimasEndpoint();
        setupLocalizacionesEndpoint();
        setupOfertaMasBarataEndpoint();
        setupOfertasMasBaratasPorClimaFechaEndpoint();
    }

    private void configureStaticFiles() {
        staticFiles.location("/public");
    }

    private void setupClimasEndpoint() {
        get("/climas", this::getTiposClima);
    }

    private String getTiposClima(Request request, Response response) {
        response.type("application/json");
        return new Gson().toJson(Arrays.asList("COLD", "RAINY", "WARM", "SNOWY", "CLEAR"));
    }

    private void setupLocalizacionesEndpoint() {
        get("/localizaciones", this::getLocalizaciones);
    }

    private String getLocalizaciones(Request request, Response response) {
        response.type("application/json");
        String tipoClima = request.queryParams("clima");
        String fecha = request.queryParams("fecha");
        Map<String, Weather> localizaciones = dataMartConsultant.getLocationsByWeatherAndDate(tipoClima, fecha);
        System.out.println(localizaciones);
        return new Gson().toJson(localizaciones);
    }

    private void setupOfertaMasBarataEndpoint() {
        get("/oferta/:localizacion/:fecha", this::getOfertaMasBarata);
    }

    private String getOfertaMasBarata(Request request, Response response) {
        response.type("application/json");
        String localizacion = request.params(":localizacion");
        String fechaReserva = request.params(":fecha");
        HotelOffer tarifaMasBarata = dataMartConsultant.getCheapestOffer(localizacion, fechaReserva);
        return new Gson().toJson(tarifaMasBarata);
    }

    private void setupOfertasMasBaratasPorClimaFechaEndpoint() {
        get("/ofertas-mas-baratas", this::getOfertasMasBaratasPorClimaFecha);
    }

    private String getOfertasMasBaratasPorClimaFecha(Request request, Response response) {
        response.type("application/json");
        String tipoClima = request.queryParams("clima");
        String fecha = request.queryParams("fecha");

        Offer ofertasMasBaratas = dataMartConsultant.getCheapestHotelOffersByWeatherAndDate(tipoClima, fecha);

        return new Gson().toJson(ofertasMasBaratas);
    }

}
