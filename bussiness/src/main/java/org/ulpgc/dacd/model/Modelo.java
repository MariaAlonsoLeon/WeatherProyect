package org.ulpgc.dacd.model;

import org.neo4j.driver.*;

import java.time.Instant;

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
}