package org.ulpgc.dacd.view;

import org.ulpgc.dacd.control.LocationRecommendationService;
import spark.Request;
import spark.Response;

import java.util.Arrays;
import java.util.Set;

import static spark.Spark.*;

public class HotelRecommendationAPI {
    private final LocationRecommendationService locationRecommendationService;

    public HotelRecommendationAPI(LocationRecommendationService locationRecommendationService) {
        this.locationRecommendationService = locationRecommendationService;
    }

    public void init() {
        staticFiles.location("/public");

        get("/climas", (req, res) -> {
            try {
                return getTiposClima(req, res);
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "Error interno del servidor: " + e.getMessage();
            }
        });

        get("/localizaciones/:clima", (req, res) -> {
            try {
                return getLocalizaciones(req, res);
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "Error interno del servidor: " + e.getMessage();
            }
        });

        get("/oferta/:localizacion/:fecha", (req, res) -> {
            try {
                return getOfertaMasBarata(req, res);
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "Error interno del servidor: " + e.getMessage();
            }
        });
    }

    private String getTiposClima(Request request, Response response) {
        response.type("text/html");
        return "<html><body><h1>Tipos de Clima</h1><p>COLD, RAINY, WARM, SNOWY, CLEAR</p></body></html>";
    }

    private String getLocalizaciones(Request request, Response response) {
        response.type("text/html");
        String tipoClima = request.params(":clima");
        Set<String> localizaciones = locationRecommendationService.obtenerLocalizacionesPorTipoClima(tipoClima);
        StringBuilder htmlResponse = new StringBuilder("<html><body><h1>Localizaciones para el clima " + tipoClima + "</h1><ul>");
        for (String localizacion : localizaciones) {
            htmlResponse.append("<li>").append(localizacion).append("</li>");
        }
        htmlResponse.append("</ul></body></html>");
        return htmlResponse.toString();
    }

    private String getOfertaMasBarata(Request request, Response response) {
        response.type("text/html");
        String localizacion = request.params(":localizacion");
        String fechaReserva = request.params(":fecha");
        double tarifaMasBarata = locationRecommendationService.obtenerTarifaMasBarata(localizacion, fechaReserva);

        return "<html><body><h1>Oferta Más Barata</h1><p>Localización: " + localizacion +
                "<br>Fecha de Reserva: " + fechaReserva +
                "<br>Tarifa Más Barata: " + tarifaMasBarata + "</p></body></html>";
    }
}
