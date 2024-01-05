package org.ulpgc.dacd.control;

import org.ulpgc.dacd.view.model.HotelOffer;
import org.ulpgc.dacd.view.model.LocationOfferByWeather;
import org.ulpgc.dacd.view.model.Offer;
import org.ulpgc.dacd.view.model.Weather;

import java.sql.*;
import java.util.*;

public class DataMartConsultant {

    private final String dbPath;

    public DataMartConsultant(String dbPath) {
        this.dbPath = dbPath;
    }

    public Offer getCheapestHotelOffersByWeatherAndDate(String weatherType, String date) {
        List<LocationOfferByWeather> locationOfferByWeathers = new ArrayList<>();
        Map<String, Weather> weatherByLocation = getLocationsByWeatherAndDate(weatherType, date);
        for (String location : weatherByLocation.keySet()) {
            HotelOffer cheapestOffer = getCheapestOffer(location, date);
            Weather weather = weatherByLocation.get(location);
            locationOfferByWeathers.add(new LocationOfferByWeather(location, cheapestOffer, weather));
        }
        return new Offer(date, weatherType, locationOfferByWeathers);
    }

    public HotelOffer getCheapestOffer(String location, String date) {
        try (Connection connection = connect(dbPath)) {
            String tableName = "HotelOffers";
            if (!isDateTimeAndLocationInTable(connection, tableName, location, date)) return null;
            String query = buildHotelOfferQuery(tableName);
            try (PreparedStatement preparedStatement = prepareStatementForHotelOffer(connection, query, location, date);
                 ResultSet resultSet = executeHotelOfferQuery(preparedStatement)) {
                return processHotelOfferResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildHotelOfferQuery(String tableName) {
        return "SELECT price, name, companyName FROM " + tableName + " WHERE location = ? AND date = ? ORDER BY price ASC LIMIT 1";
    }

    private PreparedStatement prepareStatementForHotelOffer(Connection connection, String query, String location, String date) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, location);
        preparedStatement.setString(2, date);
        return preparedStatement;
    }

    private ResultSet executeHotelOfferQuery(PreparedStatement preparedStatement) throws SQLException {
        return preparedStatement.executeQuery();
    }

    private HotelOffer processHotelOfferResultSet(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return new HotelOffer(resultSet.getString("name"), resultSet.getString("companyName"), resultSet.getDouble("price"));
        }
        return null;
    }

    public Map<String, Weather> getLocationsByWeatherAndDate(String weatherType, String date) {
        Map<String, Weather> weatherMap = new HashMap<>();
        try (Connection connection = connect(dbPath)) {
            String tableName = "Weathers";
            if (!hasWeatherType(connection, tableName, weatherType, date)) return weatherMap;
            String query = buildWeatherQuery(tableName);
            try (PreparedStatement preparedStatement = prepareStatementForWeather(connection, query, weatherType, date);
                 ResultSet resultSet = executeWeatherQuery(preparedStatement)) {
                processWeatherResultSet(resultSet, weatherMap);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return weatherMap;
    }

    private String buildWeatherQuery(String tableName) {
        return "SELECT location, temperature, rainProbability FROM " + tableName + " WHERE weatherType = ? AND date = ?";
    }

    private void processWeatherResultSet(ResultSet resultSet, Map<String, Weather> weatherMap) throws SQLException {
        while (resultSet.next()) {
            Weather weather = createWeatherFromResultSet(resultSet);
            weatherMap.put(resultSet.getString("location"), weather);
        }
    }

    private Weather createWeatherFromResultSet(ResultSet resultSet) throws SQLException {
        return new Weather(resultSet.getFloat("temperature"), resultSet.getFloat("rainProbability"));
    }

    private PreparedStatement prepareStatementForWeather(Connection connection, String query, String weatherType, String date) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, weatherType);
        preparedStatement.setString(2, date);
        return preparedStatement;
    }

    private ResultSet executeWeatherQuery(PreparedStatement preparedStatement) throws SQLException {
        return preparedStatement.executeQuery();
    }

    private boolean hasWeatherType(Connection connection, String tableName, String weatherType, String date) throws SQLException {
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE date = ? AND weatherType = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, date);
            preparedStatement.setString(2, weatherType);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    private boolean isDateTimeAndLocationInTable(Connection connection, String tableName, String location, String predictionTime) throws SQLException {
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE location = ? AND date = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, location);
            preparedStatement.setString(2, predictionTime);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    private Connection connect(String dbPath) throws SQLException {
        String url = "jdbc:sqlite:" + dbPath;
        return DriverManager.getConnection(url);
    }
}
