package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;
import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLiteWeatherStore implements WeatherStore {
    private static final Logger logger = Logger.getLogger(SQLiteWeatherStore.class.getName());
    private final String databaseURL;

    public SQLiteWeatherStore(String databaseURL) {
        this.databaseURL = databaseURL;
    }

    @Override
    public void save(List<Weather> weatherList) {
        if (weatherList.isEmpty()) {
            return;
        }
        try (Connection connection = DriverManager.getConnection(databaseURL)) {
            Location location = weatherList.get(0).getLocation();
            String tableName = buildTableName(location);
            createTableIfNotExists(connection, tableName);
            processWeatherList(connection, tableName, weatherList);
        } catch (SQLException e) {
            handleSQLException("Error saving weather data.", e);
        }
    }

    private String buildTableName(Location location) {
        return location.getName().replace(" ", "_");
    }

    private void processWeatherList(Connection connection, String tableName, List<Weather> weatherList) {
        weatherList.forEach(weather -> {
            try {
                upsertRecord(connection, tableName, weather);
            } catch (SQLException e) {
                handleSQLException("Error processing weather data.", e);
            }
        });
    }

    private void upsertRecord(Connection connection, String tableName, Weather weather) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            if (searchDate(tableName, statement, weather.getTs().toString())){
                updateValue(statement, weather, tableName);
            } else {
                insert(statement, weather, tableName);
            }
        } catch (SQLException e) {
            handleSQLException("Error executing SQL query.", e);
        }
    }

    public static boolean searchDate(String tableName, Statement statement, String date) {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * from " + tableName);
            while (resultSet.next()) {
                String columnValue = resultSet.getString(1);
                if (date.equals(columnValue)) {
                    resultSet.close();
                    return true;
                }
            }
            resultSet.close();
            return false;
        } catch (SQLException e) {
            handleSQLException("Error searching date in table " + tableName, e);
            return false;
        }
    }
    private void updateValue(Statement statement, Weather weather, String tablename) {
        String updateQuery = "UPDATE " + tablename + " SET " +
                "temperature = ?, " +
                "rain = ?, " +
                "humidity = ?, " +
                "cloud = ?, " +
                "wind_speed = ?, " +
                "ss = ?, " +
                "predictionTime = ? WHERE date = ?";
        try (PreparedStatement preparedStatement = statement.getConnection().prepareStatement(updateQuery)) {
            preparedStatement.setFloat(1, weather.getTemperature());
            preparedStatement.setFloat(2, weather.getRain());
            preparedStatement.setInt(3, weather.getHumidity());
            preparedStatement.setFloat(4, weather.getClouds());
            preparedStatement.setFloat(5, weather.getWindSpeed());
            preparedStatement.setString(6, weather.getTs().toString());
            preparedStatement.setString(7, weather.getSs().toString());
            preparedStatement.setString(8, weather.getPredictionTime().toString());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insert(Statement statement, Weather weather, String tablename) {
        String insertQuery = "INSERT INTO " + tablename + " VALUES (?, ?, ?, ?, ?, ?. ?, ?)";
        try (PreparedStatement preparedStatement = statement.getConnection().prepareStatement(insertQuery)) {
            preparedStatement.setString(1, weather.getTs().toString());
            preparedStatement.setFloat(2, weather.getTemperature());
            preparedStatement.setFloat(3, weather.getRain());
            preparedStatement.setInt(4, weather.getHumidity());
            preparedStatement.setFloat(5, weather.getClouds());
            preparedStatement.setFloat(6, weather.getWindSpeed());
            preparedStatement.setString(7, weather.getSs().toString());
            preparedStatement.setString(8, weather.getPredictionTime().toString());

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTableIfNotExists(Connection connection, String tableName) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName +
                " (date TEXT PRIMARY KEY, temperature REAL, rain REAL, humidity INTEGER, cloud INTEGER, wind_speed REAL, ss TEXT, predictionTime TEXT)";
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            handleSQLException("Error creating table.", e);
        }
    }

    private static void handleSQLException(String message, SQLException e) {
        logger.log(Level.SEVERE, message, e);
    }
}