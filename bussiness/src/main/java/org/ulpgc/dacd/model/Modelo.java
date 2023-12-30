package org.ulpgc.dacd.model;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.HashSet;
import java.util.Set;

public class Modelo implements AutoCloseable {
    private final Driver driver;

    public Modelo(String uri, String user, String password) {
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public void updateHotelNode(HotelOfferNode hotelNode) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MERGE (h:Hotel {nombre: $hotelName}) " +
                                "MERGE (d:Fecha {valor: $date}) " +
                                "MERGE (h)-[:SE_ENCUENTRA_EN]->(l: Ubicacion {nombre: $location}) " +
                                "MERGE (h)-[:OFRECE]->(o:Oferta {companyName: $companyName})-[:EN_FECHA]->(d) " +
                                "ON CREATE SET o.tax = $tax " +
                                "ON MATCH SET o.tax = $tax",
                        Values.parameters(
                                "hotelName", hotelNode.name(),
                                "date", hotelNode.predictionTime(),
                                "tax", hotelNode.tax(),
                                "companyName", hotelNode.companyName(),
                                "location", hotelNode.locationName()
                        ));
                return null;
            });
        }
    }

    public void updateWeatherNode(WeatherNode weatherNode) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MERGE (l:Ubicacion {nombre: $location}) " +
                                "MERGE (d:Fecha {valor: $date}) " +
                                "MERGE (l)-[:TIENE_PREVISION]->(w:Weather)-[:EN_FECHA]->(d) " +
                                "ON CREATE SET w.weatherType = $weatherType, w.clouds = $clouds, w.rain = $rain, w.temperature = $temperature, w.humidity = $humidity " +
                                "ON MATCH SET w.weatherType = $weatherType, w.clouds = $clouds, w.rain = $rain, w.temperature = $temperature, w.humidity = $humidity",
                        Values.parameters(
                                "location", weatherNode.location(),
                                "date", weatherNode.predictionTime(),
                                "temperature", weatherNode.temperature(),
                                "humidity", weatherNode.humidity(),
                                "clouds", weatherNode.clouds(),
                                "rain", weatherNode.rainProbability(),
                                "weatherType", weatherNode.weatherType().toString()
                        ));
                return null;
            });
        }
    }
    public void updateOfferNode(OfferNode offerNode) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MERGE (h:Hotel {name: $hotelName}) " +
                                "MERGE (l:Ubicacion {nombre: $location}) " +
                                "MERGE (d:Fecha {valor: $date}) " +
                                "MERGE (h)-[:SE_ENCUENTRA_EN]->(l) " +
                                "MERGE (h)-[:OFRECE]->(o:Oferta {companyName: $companyName})-[:EN_FECHA]->(d) " +
                                "ON CREATE SET o.precio_por_noche = $tax " +
                                "ON MATCH SET o.precio_por_noche = $tax",
                        Values.parameters(
                                "hotelName", offerNode.hotelName(),
                                "location", offerNode.locationName(),
                                "date", offerNode.predictionTime(),
                                "tax", offerNode.tax(),
                                "companyName", offerNode.companyName()
                        ));
                return null;
            });
        }
    }

    public Set<String> obtenerLocalizacionesPorTipoClima(String weatherType, String predictionTime) {
        try (Session session = driver.session()) {
            Result result = session.run("MATCH (l:Ubicacion)-[:TIENE_PREVISION]->(w:Weather)-[:EN_FECHA]->(d:Fecha {valor: $predictionTime}) WHERE w.weatherType = $weatherType RETURN l.nombre AS Location", Values.parameters("weatherType", weatherType, "predictionTime", predictionTime));
            Set<String> localizaciones = new HashSet<>();
            while (result.hasNext()) {
                Record record = result.next();
                localizaciones.add(record.get("Location").asString());
            }
            return localizaciones;
        }
    }

    public double obtenerTarifaMasBarata(String locationName, String predictionTime) {
        try (Session session = driver.session()) {
            Result result = session.run(
                    "MATCH (h:Hotel)-[:SE_ENCUENTRA_EN]->(l:Ubicacion {nombre: $location}) " +
                            "MATCH (o:Oferta)-[:EN_FECHA]->(d:Fecha {valor: $predictionTime}) " +
                            "MATCH (h)-[:OFRECE]->(o)-[:EN_FECHA]->(d) " +
                            "RETURN MIN(o.tax) AS tax",
                    Values.parameters("location", locationName, "predictionTime", predictionTime)
            );
            return result.single().get("tax", 0.0);
        }
    }

    @Override
    public void close() {
        try {
            driver.close();
        } catch (Exception e) {
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