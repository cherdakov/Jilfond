package com.jilfond.bot.databases;

import com.jilfond.bot.objects.BotUser;
import com.jilfond.bot.objects.Apartment;
import com.jilfond.bot.sessions.Session;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteOpenMode;

import java.sql.*;
import java.util.LinkedList;

public class Database {
    private Connection connection = getConnection();
    private final String databaseFileName = "database.s3db";


    public Database() throws SQLException {

    }


    public void addApartment(Apartment apartment, boolean added) throws SQLException {
        String sql =
                "insert into apartments (street, houseNumber, number, price, square, seller, added) " +
                        "values (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, apartment.getStreet());
        preparedStatement.setString(2, apartment.houseNumber);
        preparedStatement.setInt(3, apartment.number);
        preparedStatement.setInt(4, apartment.price);
        preparedStatement.setInt(5, apartment.square);
        preparedStatement.setInt(6, apartment.seller);
        preparedStatement.setBoolean(7, added);
        preparedStatement.execute();
        apartment.databaseId = preparedStatement.getGeneratedKeys().getInt(1);
        if (apartment.photos != null) {
            for (String photo : apartment.photos) {
                addPhotosToApartment(apartment.databaseId, photo);
            }
        }
    }

    public void addApartment(Apartment apartment) throws SQLException {
        addApartment(apartment, true);
    }

    void addPhotosToApartment(Integer apartmentDatabaseId, String photo) throws SQLException {
        Statement statement = connection.createStatement();
        String sql = "insert into photos (apartmentId, photo) " +
                "values (" + apartmentDatabaseId + ", " +
                "'" + photo + "')";
        statement.execute(sql);
    }

    public void addUserIfNotExist(BotUser user) throws SQLException {
        if (!userExist(user.telegramId)) {
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
        String sql =
                "insert into users (telegramId, firstName, lastName, userName, phoneNumber, email) " +
                        "values (?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, user.telegramId);
        preparedStatement.setString(2, user.firstName);
        preparedStatement.setString(3, user.lastName);
        preparedStatement.setString(4, user.userName);
        preparedStatement.setString(5, user.phoneNumber);
        preparedStatement.setString(6, user.email);
        preparedStatement.execute();
    }

    public boolean userExist(Integer telegramId) throws SQLException {
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
                        "set phoneNumber = '" + phoneNumber + "' " +
                        "where telegramId = " + userId;
        statement.execute(sql);
    }

    public void updateEmail(Integer userId, String email) throws SQLException {
        Statement statement = connection.createStatement();
        String sql =
                "update users " +
                        "set email = '" + email + "' " +
                        "where telegramId = " + userId;
        statement.execute(sql);
    }

    public void deletePhotosFromApartment(Integer apartmentId) throws SQLException {
        Statement statement = connection.createStatement();
        String sql =
                "delete from photos " +
                        "where apartmentId = " + apartmentId;
        statement.execute(sql);
    }

    public void deleteApartmentById(Integer apartmentId) throws SQLException {
        deletePhotosFromApartment(apartmentId);
        Statement statement = connection.createStatement();
        String sql =
                "delete from apartments " +
                        "where id = " + apartmentId;
        statement.execute(sql);
    }

    public LinkedList<Apartment> getApartmentsByTelegramId(Integer telegramId, boolean added) throws SQLException {
        LinkedList<Apartment> apartments = new LinkedList<>();
        String sql =
                "SELECT * FROM apartments " +
                        "WHERE seller = " + telegramId;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            if (resultSet.getBoolean("added") != added) {
                continue;
            }
            Apartment apartment = new Apartment();
            apartment.street = resultSet.getString("street");
            apartment.houseNumber = resultSet.getString("houseNumber");
            apartment.number = resultSet.getInt("number");
            apartment.databaseId = resultSet.getInt("id");
            apartment.price = resultSet.getInt("price");
            apartment.square = resultSet.getInt("square");
            apartment.seller = resultSet.getInt("seller");
            apartment.databaseId = resultSet.getInt("id");
            Statement photoStatement = connection.createStatement();
            String photoSql = "SELECT * FROM photos " +
                    "WHERE apartmentId = " + apartment.databaseId;
            ResultSet photoResultSet = photoStatement.executeQuery(photoSql);
            while (photoResultSet.next()) {
                String photo = photoResultSet.getString("photo");
                apartment.photos.add(photo);
            }
            apartments.add(apartment);
            photoResultSet.close();
        }
        resultSet.close();
        return apartments;
    }

    public LinkedList<Apartment> getApartmentsByTelegramId(Integer telegramId) throws SQLException {
        return getApartmentsByTelegramId(telegramId, true);
    }

    public boolean sessionExist(Integer telegramId) throws SQLException {
        String sql = "SELECT count(*) FROM states " +
                "where telegramId = " + telegramId;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        int count = resultSet.getInt(1);
        resultSet.close();
        return count == 1;
    }

    public void saveSession(Session session){

    }

}
