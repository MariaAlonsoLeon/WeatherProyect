package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.HotelOfferNode;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataMartConsultant {

    private final String dbPath;

    public DataMartConsultant(String dbPath) {
        this.dbPath = dbPath;
    }

    public Optional<Double> getCheapestOffer(String location, String date) {
        try (Connection connection = connect(dbPath)) {
            String tableName = buildTableName(location) + "_hotels";
            String query = "SELECT price FROM " + tableName + " WHERE date = ? ORDER BY price ASC LIMIT 1";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, date);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return Optional.of(resultSet.getDouble("price"));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Return a default value if no result is found
        return null;
    }

    public List<String> getLocationsWithWeatherType(String weatherType, String date) {
        List<String> locations = new ArrayList<>();

        try (Connection connection = connect(dbPath)) {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet tables = meta.getTables(null, null, "%_weather", null);

            while (tables.next()) {
                String tableName = tables.getString(3);
                String location = tableName.replace("_weather", "");

                if (hasWeatherType(connection, tableName, weatherType, date)) {
                    locations.add(location);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return locations;
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

    private HotelOfferNode extractHotelOfferNode(ResultSet resultSet) throws SQLException {
        String location = resultSet.getString("location");
        String date = resultSet.getString("date");
        String name = resultSet.getString("name");
        double price = resultSet.getDouble("price");

        return new HotelOfferNode(name, price, location, date);
    }

    private Connection connect(String dbPath) throws SQLException {
        String url = "jdbc:sqlite:" + dbPath;
        return DriverManager.getConnection(url);
    }

    private String buildTableName(String locationName) {
        return locationName.replaceAll("\\s", "_");
    }
}
