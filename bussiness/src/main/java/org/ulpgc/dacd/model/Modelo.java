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

    /*public void updateHotelNode(HotelOfferNode hotelNode) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MERGE (h:Hotel {name: $hotelName}) MERGE (d:Date {value: $date}) " +
                                "MERGE (l:Location {name: $location}) MERGE (h)-[:LOCATED_IN]->(l) " +
                                "MERGE (h)-[:OFFERS]->(o:Offer {companyName: $companyName})-[:ON_DATE]->(d) " +
                                "ON CREATE SET o.tax = $tax ON MATCH SET o.tax = $tax",
                        Values.parameters(
                                "hotelName", hotelNode.name(), "date", hotelNode.predictionTime(),
                                "tax", hotelNode.tax(), "companyName", hotelNode.companyName(), "location", hotelNode.locationName()
                        ));
                return null;
            });
        }
    }*/

    public void updateWeatherNode(WeatherNode weatherNode) {
        String params = "w.weatherType = $weatherType, w.clouds = $clouds, w.rain = $rain, w.temperature = $temperature, w.humidity = $humidity";
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MERGE (l:Location {name: $location}) " +
                                "MERGE (d:Date {value: $date}) MERGE (l)-[:HAS_FORECAST]->(w:Weather)-[:ON_DATE]->(d) " +
                                "ON CREATE SET " + params + " ON MATCH SET " + params,
                        Values.parameters(
                                "location", weatherNode.location(), "date", weatherNode.predictionTime(),
                                "temperature", weatherNode.temperature(), "humidity", weatherNode.humidity(),
                                "clouds", weatherNode.clouds(), "rain", weatherNode.rainProbability(),
                                "weatherType", weatherNode.weatherType().toString()
                        ));
                return null;
            });
        }
    }

    public Set<String> getLocationsByWeatherTypeAndDate(String weatherType, String predictionTime) {
        try (Session session = driver.session()) {
            Result result = session.run("MATCH (l:Location)-[:HAS_FORECAST]->(w:Weather)-[:ON_DATE]->(d:Date {value: $predictionTime}) WHERE w.weatherType = $weatherType RETURN l.name AS Location", Values.parameters("weatherType", weatherType, "predictionTime", predictionTime));
            Set<String> locations = new HashSet<>();
            while (result.hasNext()) {
                Record record = result.next();
                locations.add(record.get("Location").asString());
            }
            return locations;
        }
    }

    public double getCheapestRate(String locationName, String predictionTime) {
        try (Session session = driver.session()) {
            Result result = session.run(
                    "MATCH (h:Hotel)-[:LOCATED_IN]->(l:Location {name: $location}) " +
                            "MATCH (o:Offer)-[:ON_DATE]->(d:Date {value: $predictionTime}) " +
                            "MATCH (h)-[:OFFERS]->(o)-[:ON_DATE]->(d) RETURN MIN(o.tax) AS tax",
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

    public void clearGraph() {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (n) DETACH DELETE n");
                return null;
            });
        }
    }
}