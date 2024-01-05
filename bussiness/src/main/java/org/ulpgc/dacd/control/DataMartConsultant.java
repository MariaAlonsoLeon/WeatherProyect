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
        try (Connection connection = connect(dbPath)) {
            String weatherTableName = "Weathers";
            String hotelTableName = "HotelOffers";

            // Obtener localizaciones que cumplen con el tipo de weather y fecha
            Map<String, Weather> weatherByLocation = getLocationsByWeatherAndDate(weatherType, date);
            Set<String> locations = weatherByLocation.keySet();

            // Para cada localización, obtener la oferta más barata de hotel
            for (String location : locations) {
                // Verificar si hay registros de hotel para la localización dada

                // Obtener la oferta más barata de hotel para la localización y fecha dados

                HotelOffer cheapestOffer = getCheapestOffer(location, date);
                System.out.println("Oferasss");
                Weather weather = weatherByLocation.get(location);
                locationOfferByWeathers.add(new LocationOfferByWeather(location, cheapestOffer, weather));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Offer offer = new Offer(date, weatherType, locationOfferByWeathers);
        return offer;
    }

    public HotelOffer getCheapestOffer(String location, String date) {
        try (Connection connection = connect(dbPath)) {
            String tableName = "HotelOffers";
            if (!isDateTimeAndLocationInTable(connection, tableName, location, date)) {
                return null;
            }

            System.out.println("HotelOffer");
            String query = "SELECT price, name, companyName FROM " + tableName + " WHERE location = ? AND date = ? ORDER BY price ASC LIMIT 1";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, location);
                preparedStatement.setString(2, date);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        HotelOffer hotelOffer = new HotelOffer(resultSet.getString("name"), resultSet.getString("companyName"), resultSet.getDouble("price"));
                        System.out.println(hotelOffer);
                        return hotelOffer;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Return a default value if no result is found
        return null;
    }

    public Map<String, Weather> getLocationsByWeatherAndDate(String weatherType, String date) {
        Map<String, Weather> weatherMap = new HashMap<>();
        try (Connection connection = connect(dbPath)) {
            String tableName = "Weathers";
            if (!hasWeatherType(connection, tableName, weatherType, date)) {
                System.out.println("Estoy aqui");
                return weatherMap;
            }
            String query = "SELECT location, temperature, rainProbability FROM " + tableName + " WHERE weatherType = ? AND date = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, weatherType);
                preparedStatement.setString(2, date);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Weather weather = new Weather(resultSet.getFloat("temperature"), resultSet.getFloat("rainProbability"));
                        weatherMap.put(resultSet.getString("location"), weather);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return weatherMap;
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

    private boolean isDateTimeAndLocationInTable(Connection connection, String tableName, String location, String predictionTime)
            throws SQLException {
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE location = ? AND date = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, location);
            preparedStatement.setString(2, predictionTime);
            return countDateTimeInTable(preparedStatement);
        }
    }

    private boolean countDateTimeInTable(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            return resultSet.next() && resultSet.getInt(1) > 0;
        }
    }

    private Connection connect(String dbPath) throws SQLException {
        String url = "jdbc:sqlite:" + dbPath;
        return DriverManager.getConnection(url);
    }

}
