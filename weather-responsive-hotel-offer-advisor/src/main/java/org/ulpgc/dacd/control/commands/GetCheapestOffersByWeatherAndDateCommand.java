package org.ulpgc.dacd.control.commands;

import org.ulpgc.dacd.control.exceptions.DataMartConsultingException;
import org.ulpgc.dacd.view.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetCheapestOffersByWeatherAndDateCommand implements Command {

    private final String dbPath;

    public GetCheapestOffersByWeatherAndDateCommand(String dbPath) {
        this.dbPath = dbPath;
    }

    @Override
    public Offer execute(List<String> params) throws DataMartConsultingException {
        String weatherType = params.get(0);
        String date = params.get(1);
        List<LocationOfferByWeather> locationOffers = new ArrayList<>();
        try (Connection connection = connect(dbPath)) {
            if (hasWeatherType(connection, "Weathers", weatherType, date)) {
                String query = buildOfferQuery("HotelOffers", "Weathers");
                processOfferResultSet(executeOfferQuery(prepareStatementForOffer(connection, query, weatherType, date)), locationOffers);
            }
        } catch (SQLException e) {
            throw new DataMartConsultingException("Error getting cheapest offers", e);
        }
        return new Offer(date, weatherType, locationOffers);
    }

    private Connection connect(String dbPath) throws SQLException {
        String url = "jdbc:sqlite:" + dbPath;
        return DriverManager.getConnection(url);
    }

    private String buildOfferQuery(String hotelTableName, String weatherTableName) {
        return "SELECT h.location, h.name, h.companyName, h.price, w.temperature, w.rainProbability FROM " + hotelTableName +
                " h INNER JOIN " + weatherTableName +
                " w ON h.location = w.location AND h.date = w.date WHERE w.weatherType = ? AND w.date = ? ORDER BY h.price";
    }

    private void processOfferResultSet(ResultSet resultSet, List<LocationOfferByWeather> locationOffers) throws SQLException {
        Map<String, LocationOfferByWeather> locationOfferMap = new HashMap<>();

        while (resultSet.next()) {
            processLocationOfferResultSet(resultSet, locationOffers, locationOfferMap);
        }
    }

    private void processLocationOfferResultSet(ResultSet resultSet, List<LocationOfferByWeather> locationOffers, Map<String, LocationOfferByWeather> locationOfferMap) throws SQLException {
        String locationName = resultSet.getString("location");

        LocationOfferByWeather locationOffer = getLocationOffer(locationName, resultSet, locationOfferMap);
        locationOfferMap.putIfAbsent(locationName, locationOffer);

        HotelOffer hotelOffer = createHotelOfferFromResultSet(resultSet);
        Weather weather = createWeatherFromResultSet(resultSet);

        locationOffers.add(new LocationOfferByWeather(locationName, hotelOffer, weather));
    }

    private LocationOfferByWeather getLocationOffer(String locationName, ResultSet resultSet, Map<String, LocationOfferByWeather> locationOfferMap) throws SQLException {
        return locationOfferMap.computeIfAbsent(locationName, key -> {
            try {
                return createLocationOfferFromResultSet(resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private HotelOffer createHotelOfferFromResultSet(ResultSet resultSet) throws SQLException {
        String hotelName = resultSet.getString("name");
        String companyName = resultSet.getString("companyName");
        double cost = resultSet.getDouble("price");
        return new HotelOffer(hotelName, companyName, cost);
    }

    private Weather createWeatherFromResultSet(ResultSet resultSet) throws SQLException {
        float temperature = resultSet.getFloat("temperature");
        float rainProbability = resultSet.getFloat("rainProbability");
        return new Weather(temperature, rainProbability);
    }

    private LocationOfferByWeather createLocationOfferFromResultSet(ResultSet resultSet) throws SQLException {
        String locationName = resultSet.getString("location");
        return new LocationOfferByWeather(locationName, null, null);
    }

    private PreparedStatement prepareStatementForOffer(Connection connection, String query, String weatherType, String date) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, weatherType);
        preparedStatement.setString(2, date);
        return preparedStatement;
    }

    private ResultSet executeOfferQuery(PreparedStatement preparedStatement) throws SQLException {
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
