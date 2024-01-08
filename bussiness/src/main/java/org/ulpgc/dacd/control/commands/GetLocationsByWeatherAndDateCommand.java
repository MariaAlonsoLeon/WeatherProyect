package org.ulpgc.dacd.control.commands;

import org.ulpgc.dacd.control.exceptions.DataMartConsultingException;
import org.ulpgc.dacd.view.model.Output;
import org.ulpgc.dacd.view.model.WeatherByLocation;
import org.ulpgc.dacd.view.model.WeathersByLocations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GetLocationsByWeatherAndDateCommand implements Command {
    private final String dbPath;

    public GetLocationsByWeatherAndDateCommand(String dbPath) {
        this.dbPath = dbPath;
    }

    @Override
    public Output execute(List<String> params) throws DataMartConsultingException {
        String weatherType = params.get(0);
        String date = params.get(1);
        List<WeatherByLocation> weatherByLocations = new ArrayList<>();
        try (Connection connection = connect(dbPath)) {
            String tableName = "Weathers";
            if (hasWeatherType(connection, tableName, weatherType, date)) {
                String query = buildWeatherQuery(tableName);
                processWeatherResultSet(executeWeatherQuery(prepareStatementForWeather(connection, query, weatherType, date)), weatherByLocations);
            }
        } catch (SQLException e) {
            throw new DataMartConsultingException("Error getting weather by locations", e);
        }
        return new WeathersByLocations(date, weatherType, weatherByLocations);
    }

    private Connection connect(String dbPath) throws SQLException {
        String url = "jdbc:sqlite:" + dbPath;
        return DriverManager.getConnection(url);
    }

    private String buildWeatherQuery(String tableName) {
        return "SELECT location, temperature, rainProbability, windSpeed, humidity FROM " + tableName + " WHERE weatherType = ? AND date = ?";
    }

    private void processWeatherResultSet(ResultSet resultSet, List<WeatherByLocation> weatherByLocations) throws SQLException {
        while (resultSet.next()) {
            weatherByLocations.add(createWeatherFromResultSet(resultSet));
        }
    }

    private WeatherByLocation createWeatherFromResultSet(ResultSet resultSet) throws SQLException {
        return new WeatherByLocation(
                resultSet.getString("location"),
                resultSet.getFloat("temperature"),
                resultSet.getFloat("rainProbability"),
                resultSet.getFloat("windSpeed"),
                resultSet.getInt("humidity")
        );
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
}
