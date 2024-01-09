package org.ulpgc.dacd.control.commands;

import org.ulpgc.dacd.control.exceptions.DataMartConsultingException;
import org.ulpgc.dacd.view.model.HotelOffer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class GetCheapestOfferByLocationAndDateCommand implements Command {
    String dbPath;

    public GetCheapestOfferByLocationAndDateCommand(String dbPath) {
        this.dbPath = dbPath;
    }

    @Override
    public HotelOffer execute(List<String> params) throws DataMartConsultingException {
        String location = params.get(0);
        String date = params.get(1);
        try (Connection connection = connect(dbPath)) {
            String tableName = "HotelOffers";
            if (!isDateTimeAndLocationInTable(connection, tableName, location, date)) return null;
            String query = buildHotelOfferQuery(tableName);
            try (PreparedStatement preparedStatement = prepareStatementForHotelOffer(connection, query, location, date);
                 ResultSet resultSet = executeHotelOfferQuery(preparedStatement)) {
                return processHotelOfferResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataMartConsultingException("Error getting cheapest offer", e);
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
}
