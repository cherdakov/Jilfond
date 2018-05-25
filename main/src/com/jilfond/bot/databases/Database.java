package com.jilfond.bot.databases;

import com.jilfond.bot.BotUser;
import com.jilfond.bot.objects.Apartment;

import java.sql.*;
import java.util.LinkedList;

public class Database {
    private Connection connection;
    private final String databaseFileName = "database.s3db";



    public Database() throws SQLException {
        connection = getConnection();
    }

    public LinkedList<Apartment> getApartments(Integer databaseId) throws SQLException {
        String sql =
                "SELECT * FROM apartments" +
                        "WHERE id = " + Integer.toString(databaseId);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        LinkedList<Apartment> apartmentLinkedList = new LinkedList<>();
        while (resultSet.next()) {
            Apartment apartment = new Apartment();
            String street = resultSet.getString("street");
            Integer number = resultSet.getInt("number");
            Integer price = resultSet.getInt("price");
        }
        return apartmentLinkedList;
    }

    public void addApartment(Apartment apartment) {
    }

    public void addUserIfNotExist(BotUser user) throws SQLException {
        if (!exist(user.telegramId)) {
            addUser(user);
        }
    }

    public BotUser getBotUserByTelegramId(Integer telegramId) throws SQLException {
        BotUser botUser = new BotUser();
        Statement statement = connection.createStatement();
        String sql =
                "select * from users " +
                "where telegramId = " + telegramId;
        ResultSet resultSet = statement.executeQuery(sql);
        botUser.telegramId = resultSet.getInt("telegramId");
        botUser.databaseId = resultSet.getInt("id");
        botUser.firstName = resultSet.getString("firstName");
        botUser.lastName = resultSet.getString("lastName");
        botUser.userName = resultSet.getString("userName");
        botUser.email = resultSet.getString("email");
        botUser.phoneNumber = resultSet.getString("phoneNumber");
        return botUser;
    }

    public void addUser(BotUser user) throws SQLException {
        Statement statement = connection.createStatement();
        String sql =
                "insert into users (telegramId, firstName, lastName, userName, phoneNumber, email) " +
                        "values " + user.getValuesForDB();
        statement.execute(sql);
    }

    public boolean exist(Integer telegramId) throws SQLException {
        Statement statement = connection.createStatement();
        String sql =
                "select count(*) from users " +
                "where telegramId = " + telegramId;
        ResultSet resultSet = statement.executeQuery(sql);
        return resultSet.getInt(1) == 1;
    }

    public void deleteUserByTelegramId(Integer telegramId) throws SQLException {
        Statement statement = connection.createStatement();
        String sql =
                "delete from users " +
                "where telegramId = "+ telegramId;
        statement.execute(sql);
    }

    Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + databaseFileName);
    }
}
