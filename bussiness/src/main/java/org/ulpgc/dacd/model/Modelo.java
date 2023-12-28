package org.ulpgc.dacd.model;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class Modelo implements AutoCloseable {
    private final Driver driver;

    public Modelo(String uri, String user, String password) {
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }


    public void updateHotelNode(HotelPriceNode hotelNode) {
        System.out.println("Hola " + hotelNode.locationName());
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                // Crear o actualizar nodos y relaciones según la lógica de tu aplicación
                tx.run("MERGE (h:Hotel {name: $name}) " +
                                "MERGE (l:Location {locationName: $location}) " +
                                "MERGE (d:Date {date: $date}) " +
                                "MERGE (h)-[:AT]->(l) " +
                                "MERGE (l)-[:HAS_HOTEL]->(d) " +
                                //"MERGE (h)-[:OFFERS]->(d) " +
                                "SET h.tax = $tax",
                        Values.parameters(
                                "name", hotelNode.name(),
                                "location", hotelNode.locationName(),
                                "date", hotelNode.predictionTime(),
                                "tax", hotelNode.tax()
                        ));
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
    public Set<String> obtenerLocalizacionesPorTipoClima(String weatherType) {
        try (Session session = driver.session()) {
            Result result = session.run("MATCH (l:Location)-[:HAS_FORECAST]->(d:Date) WHERE l.weatherType = $weatherType RETURN l.name AS Location", Values.parameters("weatherType", weatherType));
            Set<String> localizaciones = new HashSet<>();  // Utilizamos un Set para evitar duplicados
            while (result.hasNext()) {
                Record record = result.next();
                localizaciones.add(record.get("Location").asString());
            }
            return localizaciones;
        }
    }


    /*public double obtenerTarifaMasBarata(String locationName, String predictionTime) {
        try (Session session = driver.session()) {
            Result result = session.run(
                    "MATCH (h:Hotel)-[:AT]->(l:Location {locationName: $location})-[:OFFERS]->(d:Date {date: $predictionTime}) " +
                            "RETURN h.name AS hotelName, MIN(h.tax) AS minTax",
                    Values.parameters("location", locationName, "predictionTime", predictionTime));

            if (result.hasNext()) {
                Record record = result.single();  // Utiliza .single() para obtener un solo registro
                if (record != null && !record.get("minTax").isNull()) {
                    return record.get("minTax").asDouble();
                }
            }
        }
        return Double.MAX_VALUE;  // Valor predeterminado si no se encuentra ninguna tarifa
    }*/
    public double obtenerTarifaMasBarata(String locationName, String predictionTime) {
        try (Session session = driver.session()) {
            Result result = session.run(
                    "MATCH (h:Hotel)-[:AT]->(l:Location {locationName: $location})-[:HAS_HOTEL]->(d:Date {date: $predictionTime}) " +
                            "RETURN h.name AS hotelName, MIN(h.tax) AS minTax",
                    Values.parameters("location", locationName, "predictionTime", predictionTime));

            if (result.hasNext()) {
                Record record = result.single();
                if (record != null && !record.get("minTax").isNull()) {
                    return record.get("minTax").asDouble();
                }
            }
        }
        return Double.MAX_VALUE;
    }


    @Override
    public void close() {
        try {
            driver.close();
        } catch (Exception e) {
            // Manejar cualquier excepción que pueda ocurrir al cerrar la conexión
            e.printStackTrace();
        }
    }

    public void limpiarGrafo() {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (n) DETACH DELETE n");
                return null;
            });
        }
    }
}