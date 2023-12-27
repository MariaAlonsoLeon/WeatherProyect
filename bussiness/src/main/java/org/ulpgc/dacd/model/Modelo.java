package org.ulpgc.dacd.model;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Modelo {
    private final Driver driver;

    public Modelo(String uri, String user, String password) {
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public void close() {
        driver.close();
    }

    public void updateHotelNode(String hotelName, double tax, String location) {
        String date = Instant.now().toString();
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                // Crear o actualizar nodos y relaciones según la lógica de tu aplicación
                tx.run("MERGE (h:Hotel {name: $name}) " +
                                "MERGE (l:Location {name: $location}) " +
                                "MERGE (d:Date {date: $date}) " +
                                "MERGE (h)-[:AT]->(l) " +
                                "MERGE (h)-[:OFFERS]->(d) " +
                                "SET h.tax = $tax",
                        Values.parameters("name", hotelName, "location", location, "date", date, "tax", tax));
                return null;
            });
        }
    }

    public void updateWeatherNode(WeatherNode weatherNode) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                // Crear o actualizar nodos y relaciones según la lógica de tu aplicación
                tx.run("MERGE (l:Location {name: $location}) " +
                                "MERGE (d:Date {date: $date}) " +
                                "MERGE (l)-[:HAS_FORECAST]->(d) " +
                                "SET l.humidity = $humidity, l.temperature = $temperature, " +
                                "l.clouds = $clouds, l.rain = $rain, l.weatherType = $weatherType",
                        Values.parameters(
                                "location", weatherNode.location(),
                                "date", weatherNode.predictionTime(),
                                "humidity", weatherNode.humidity(),
                                "temperature", weatherNode.temperature(),
                                "clouds", weatherNode.clouds(),
                                "rain", weatherNode.rainProbability(),
                                "weatherType", weatherNode.weatherType().toString()
                        ));
                return null;
            });
        }
    }
    public List<String> obtenerLocalizacionesPorTipoClima(String weatherType) {
        try (Session session = driver.session()) {
            Result result = session.run("MATCH (l:Location)-[:HAS_FORECAST]->(d:Date) WHERE l.weatherType = $weatherType RETURN l.name AS Location", Values.parameters("weatherType", weatherType));
            List<String> localizaciones = new ArrayList<>();
            while (result.hasNext()) {
                Record record = result.next();
                localizaciones.add(record.get("Location").asString());
            }
            return localizaciones;
        }
    }

    public double obtenerTarifaMasBarata(String locationName, String date) {
        try (Session session = driver.session()) {
            Result result = session.run("MATCH (h:Hotel)-[:AT]->(l:Location {name: $location})-[:HAS_FORECAST]->(d:Date {date: $date}) RETURN MIN(h.tax) AS minTax", Values.parameters("location", locationName, "date", date));
            Record record = result.single();  // Utiliza .single() para obtener un solo registro

            if (record != null && !record.get("minTax").isNull()) {
                return record.get("minTax").asDouble();
            }
        }
        return Double.MAX_VALUE;  // Valor predeterminado si no se encuentra ninguna tarifa
    }
}