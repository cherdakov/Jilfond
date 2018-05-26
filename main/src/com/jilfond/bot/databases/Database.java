package com.jilfond.bot.databases;

import com.jilfond.bot.BotUser;
import com.jilfond.bot.objects.Apartment;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteOpenMode;

import java.sql.*;
import java.util.LinkedList;

public class Database {
    private Connection connection = getConnection();
    private final String databaseFileName = "database.s3db";


    public Database() throws SQLException {

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

    public void addApartment(Apartment apartment) throws SQLException {
        Statement statement = connection.createStatement();
        String sql =
                "insert into apartments (street, houseNumber, apartmentNumber, price, square, seller) " +
                        "values (" +
                        "\"" +
                        apartment.getStreet() + "\"" + ", " +
                        apartment.houseNumber + ", " +
                        apartment.apartmentNumber + ", " +
                        apartment.price + ", " +
                        apartment.square + ", " +
                        apartment.seller + ")";
        statement.execute(sql);
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
        botUser.firstName = resultSet.getString("firstName");
        botUser.lastName = resultSet.getString("lastName");
        botUser.userName = resultSet.getString("userName");
        botUser.email = resultSet.getString("email");
        botUser.phoneNumber = resultSet.getString("phoneNumber");
        resultSet.close();
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
        Integer res = resultSet.getInt(1);
        resultSet.close();
        return res == 1;
    }

    public void deleteUserByTelegramId(Integer telegramId) throws SQLException {
        Statement statement = connection.createStatement();
        String sql =
                "delete from users " +
                        "where telegramId = " + telegramId;
        statement.execute(sql);
    }

    Connection getConnection() throws SQLException {
        SQLiteConfig config = new SQLiteConfig();
        config.setOpenMode(SQLiteOpenMode.FULLMUTEX);
        return DriverManager.getConnection("jdbc:sqlite:" + databaseFileName, config.toProperties());
    }

    public void updatePhoneNumber(Integer userId, String phoneNumber) throws SQLException {
        Statement statement = connection.createStatement();
        String sql =
                "update users " +
                        "set phoneNumber = '"+phoneNumber+"' "+
                        "where telegramId = "+userId;
        statement.execute(sql);
    }

    public void updateEmail(Integer userId, String email) throws SQLException {
        Statement statement = connection.createStatement();
        String sql =
                "update users " +
                "set email = '"+email+"' "+
                "where telegramId = "+userId;
        statement.execute(sql);
    }
}
