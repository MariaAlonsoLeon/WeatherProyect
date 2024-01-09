package org.ulpgc.dacd.control;

import org.ulpgc.dacd.control.exceptions.DataMartStoreException;
import org.ulpgc.dacd.model.HotelOfferRecord;
import org.ulpgc.dacd.model.WeatherRecord;
import org.ulpgc.dacd.view.model.HotelOffer;
import java.sql.*;

public class SqLiteDataMartStore implements DataMartStore {

    private final String dbPath;

    public SqLiteDataMartStore(String dbPath) {
        this.dbPath = dbPath;
    }

    public void saveHotelOffer(HotelOfferRecord hotelOfferRecord) throws DataMartStoreException {
        String tableName = "HotelOffers";
        try (Connection connection = connect(dbPath)) {
            Statement statement = connection.createStatement();
            createHotelOfferTable(statement, tableName);
            updateOrInsertHotelOffer(hotelOfferRecord, connection, tableName);
        } catch (SQLException e) {
            throw new DataMartStoreException("Error saving hotel offer", e);
        }
    }

    public void saveWeather(WeatherRecord weatherRecord) throws DataMartStoreException {
        String tableName = "Weathers";
        try (Connection connection = connect(dbPath)) {
            Statement statement = connection.createStatement();
            createWeatherTable(statement, tableName);
            updateOrInsertWeatherData(weatherRecord, connection, tableName);
        } catch (SQLException e) {
            throw new DataMartStoreException("Error saving weather data", e);
        }
    }

    private static void createHotelOfferTable(Statement statement, String tableName) throws SQLException {
        statement.execute("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "location TEXT," +
                "date TEXT," +
                "name TEXT," +
                "companyName TEXT," +
                "price REAL," +
                "PRIMARY KEY (location, date)" +
                ");");
    }

    private static void createWeatherTable(Statement statement, String tableName) throws SQLException {
        statement.execute("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "location TEXT," +
                "date TEXT," +
                "temperature REAL," +
                "rainProbability REAL," +
                "humidity INTEGER," +
                "clouds INTEGER," +
                "windSpeed REAL," +
                "weatherType TEXT," +
                "PRIMARY KEY (location, date)" +
                ");");
    }

    private static void updateOrInsertHotelOffer(HotelOfferRecord hotelOfferRecord, Connection connection, String tableName) throws SQLException {
        String location = hotelOfferRecord.locationName();
        String predictionTime = String.valueOf(hotelOfferRecord.predictionTime());
        Double newPrice = hotelOfferRecord.tax();
        if (isDateTimeAndLocationInTable(connection, tableName, location, predictionTime)) {
            HotelOffer currentHotelOffer = getCurrentHotelOffer(connection, tableName, location, predictionTime);
            if (currentHotelOffer.companyName().equals(hotelOfferRecord.companyName()) || newPrice < currentHotelOffer.cost()) {
                updateHotelOffer(connection, hotelOfferRecord);
            }
        } else {
            insertHotelOffer(connection, hotelOfferRecord);
        }
    }

    private static void insertHotelOffer(Connection connection, HotelOfferRecord hotelOfferRecord) throws SQLException {
        String tableName = "HotelOffers";
        String insertSQL = String.format(
                "INSERT INTO %s (location, date, name, companyName, price) VALUES (?, ?, ? , ?, ?)", tableName);
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            setInsertHotelOfferParameters(preparedStatement, hotelOfferRecord);
            preparedStatement.executeUpdate();
        }
    }

    private static void updateHotelOffer(Connection connection, HotelOfferRecord hotelOfferRecord) throws SQLException {
        String tableName = "HotelOffers";
        String updateSQL = String.format(
                "UPDATE %s SET name = ?, companyName = ?, price = ? WHERE location = ? AND date = ?", tableName);
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            setUpdateHotelOfferParameters(preparedStatement, hotelOfferRecord);
            preparedStatement.executeUpdate();
        }
    }

    private static void setInsertHotelOfferParameters(PreparedStatement preparedStatement, HotelOfferRecord hotelOfferRecord) throws SQLException {
        preparedStatement.setString(1, hotelOfferRecord.locationName());
        preparedStatement.setString(2, String.valueOf(hotelOfferRecord.predictionTime()));
        preparedStatement.setString(3, hotelOfferRecord.name());
        preparedStatement.setString(4, hotelOfferRecord.companyName());
        preparedStatement.setDouble(5, hotelOfferRecord.tax());
    }

    private static void setUpdateHotelOfferParameters(PreparedStatement preparedStatement, HotelOfferRecord hotelOfferRecord) throws SQLException {
        preparedStatement.setString(1, hotelOfferRecord.name());
        preparedStatement.setString(2, hotelOfferRecord.companyName());
        preparedStatement.setDouble(3, hotelOfferRecord.tax());
        preparedStatement.setString(4, hotelOfferRecord.locationName());
        preparedStatement.setString(5, String.valueOf(hotelOfferRecord.predictionTime()));
    }

    private static HotelOffer getCurrentHotelOffer(Connection connection, String tableName, String location, String predictionTime) throws SQLException {
        String query = "SELECT name, companyName, price FROM " + tableName + " WHERE location = ? AND date = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, location);
            preparedStatement.setString(2, predictionTime);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? createHotelOfferFromResultSet(resultSet) : null;
            }
        }
    }

    private static HotelOffer createHotelOfferFromResultSet(ResultSet resultSet) throws SQLException {
        String name = resultSet.getString("name");
        String company = resultSet.getString("companyName");
        double price = resultSet.getDouble("price");
        return new HotelOffer(name, company, price);
    }

    private static void updateOrInsertWeatherData(WeatherRecord weatherRecord, Connection connection, String tableName)
            throws SQLException {
        if (isDateTimeAndLocationInTable(connection, tableName, weatherRecord.location(), weatherRecord.predictionTime())) {
            updateWeather(connection, weatherRecord);
        } else {
            insertWeather(connection, weatherRecord);
        }
    }

    private static void insertWeather(Connection connection, WeatherRecord weatherRecord) throws SQLException {
        String tableName = "Weathers";
        String insertSQL = String.format(
                "INSERT INTO %s (location, date, temperature, rainProbability, humidity, clouds, windSpeed, weatherType) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", tableName);
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            setInsertWeatherParameters(preparedStatement, weatherRecord);
            preparedStatement.executeUpdate();
        }
    }

    private static void updateWeather(Connection connection, WeatherRecord weatherRecord) throws SQLException {
        String tableName = "Weathers";
        String updateSQL = String.format(
                "UPDATE %s SET temperature = ?, rainProbability = ?, humidity = ?, clouds = ?, windSpeed = ?, weatherType = ? WHERE location = ? AND date = ?", tableName);
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            setUpdateWeatherParameters(preparedStatement, weatherRecord);
            preparedStatement.executeUpdate();
        }
    }

    private static void setInsertWeatherParameters(PreparedStatement preparedStatement, WeatherRecord weatherRecord) throws SQLException {
        preparedStatement.setString(1, weatherRecord.location());
        preparedStatement.setString(2, String.valueOf(weatherRecord.predictionTime()));
        preparedStatement.setDouble(3, weatherRecord.temperature());
        preparedStatement.setDouble(4, weatherRecord.rainProbability());
        preparedStatement.setDouble(5, weatherRecord.clouds());
        preparedStatement.setInt(6, weatherRecord.humidity());
        preparedStatement.setDouble(7, weatherRecord.windSpeed());
        preparedStatement.setString(8, String.valueOf(weatherRecord.weatherType()));
    }

    private static void setUpdateWeatherParameters(PreparedStatement preparedStatement, WeatherRecord weatherRecord) throws SQLException {
        preparedStatement.setDouble(1, weatherRecord.temperature());
        preparedStatement.setDouble(2, weatherRecord.rainProbability());
        preparedStatement.setInt(3, weatherRecord.humidity());
        preparedStatement.setDouble(4, weatherRecord.clouds());
        preparedStatement.setDouble(5, weatherRecord.windSpeed());
        preparedStatement.setString(6, String.valueOf(weatherRecord.weatherType()));
        preparedStatement.setString(7, weatherRecord.location());
        preparedStatement.setString(8, String.valueOf(weatherRecord.predictionTime()));
    }

    private static boolean isDateTimeAndLocationInTable(Connection connection, String tableName, String location, String predictionTime)
            throws SQLException {
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE location = ? AND date = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, location);
            preparedStatement.setString(2, String.valueOf(predictionTime));
            return countDateTimeInTable(preparedStatement);
        }
    }

    private static boolean countDateTimeInTable(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            return resultSet.next() && resultSet.getInt(1) > 0;
        }
    }

    public static Connection connect(String dbPath) {
        Connection connection = null;
        try {
            String url = "jdbc:sqlite:" + dbPath;
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }

    public void dropDatabase() throws DataMartStoreException {
        try (Connection connection = connect(dbPath)) {
            dropTable(connection, "HotelOffers");
            dropTable(connection, "Weathers");
        } catch (SQLException e) {
            throw new DataMartStoreException("Error dropping the database", e);
        }
    }

    private static void dropTable(Connection connection, String tableName) throws SQLException {
        String dropSQL = "DROP TABLE IF EXISTS " + tableName;
        try (PreparedStatement preparedStatement = connection.prepareStatement(dropSQL)) {
            preparedStatement.executeUpdate();
        }
    }
}
