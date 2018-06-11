package com.jilfond.bot.databases;

import com.jilfond.bot.objects.BotUser;
import com.jilfond.bot.objects.Apartment;
import com.jilfond.bot.objects.Wish;
import com.jilfond.bot.sessions.SessionDescription;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteOpenMode;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class Database {
    static private final String databaseFileName = "database.s3db";
    private Connection connection = getConnection();


    public Database() throws SQLException {

    }


    public Integer addApartment(Apartment apartment, boolean added) throws SQLException {
        String sql =
                "insert into apartments (street, houseNumber, number, price, square, seller, added) " +
                        "values (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, apartment.street);
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
        return apartment.databaseId;
    }

    public Integer addApartment(Apartment apartment) throws SQLException {
        return addApartment(apartment, true);
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

    public boolean sessionExist(Long chatId) throws SQLException {
        String sql = "SELECT count(*) FROM sessions " +
                "where chatId = " + chatId;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        int count = resultSet.getInt(1);
        resultSet.close();
        return count == 1;
    }

    public void deleteApartmentByChatId(Long chatId) throws SQLException {
        String sql = "delete from apartments " +
                "where id in (select objectId from sessions where chatId = " + chatId + ")";
        Statement statement = connection.createStatement();
        statement.execute(sql);
    }

    public void saveSession(SessionDescription session) throws SQLException {
        switch (session.type) {
            case "SELLER":
                deleteApartmentByChatId(session.chatId);
                break;
            case "BUYER":
                deleteWishByChatId(session.chatId);
                break;
        }
        deleteSession(session.chatId);
        Integer foreignKey = null;
        switch (session.type) {
            case "SELLER":
                foreignKey = addApartment((Apartment) session.object, false);
                break;
            case "BUYER":
                foreignKey = addWish((Wish) session.object, false);
                break;
        }
        String sql = "insert into sessions " +
                "(chatId, state, action, type, objectId)" +
                "values (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setLong(1, session.chatId);
        preparedStatement.setString(2, session.state);
        preparedStatement.setString(3, session.action);
        preparedStatement.setString(4, session.type);
        preparedStatement.setInt(5, foreignKey);
        preparedStatement.execute();
    }

    private void deleteWishByChatId(Long chatId) throws SQLException {
        String sql = "delete from wishes " +
                "where id in (select objectId from sessions where chatId = " + chatId + ")";
        Statement statement = connection.createStatement();
        statement.execute(sql);
    }

    public SessionDescription getSession(Long chatId) throws SQLException {
        SessionDescription sessionDescription = new SessionDescription();
        String sql = "select * from sessions " +
                "where chatId = " + chatId;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        Integer foreignKey = resultSet.getInt("objectId");
        sessionDescription.state = resultSet.getString("state");
        sessionDescription.action = resultSet.getString("action");
        sessionDescription.type = resultSet.getString("type");
        sessionDescription.chatId = resultSet.getLong("chatId");
        switch (sessionDescription.type) {
            case "BUYER":
                sessionDescription.object = getWishByWishDatabaseId(foreignKey);
                break;
            case "SELLER":
                sessionDescription.object = getApartmentByApartmentDatabaseId(foreignKey);
                break;
        }
        resultSet.close();
        return sessionDescription;
    }

    private Object getWishByWishDatabaseId(Integer wishId) throws SQLException {
        Wish wish = new Wish();
        String sql = "SELECT * FROM wishes " +
                "where added = false and id = " + wishId;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        wish.street = resultSet.getString("street");
        wish.price = resultSet.getInt("price");
        wish.square = resultSet.getInt("square");
        wish.databaseId = resultSet.getInt("id");
        wish.buyer = resultSet.getInt("buyer");
        resultSet.close();
        return wish;
    }

    public Apartment getApartmentByApartmentDatabaseId(Integer apartmentId) throws SQLException {
        Apartment apartment = new Apartment();
        String sql = "SELECT * FROM apartments " +
                "where added = false and id = " + apartmentId;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        apartment.street = resultSet.getString("street");
        apartment.houseNumber = resultSet.getString("houseNumber");
        apartment.number = resultSet.getInt("number");
        apartment.price = resultSet.getInt("price");
        apartment.square = resultSet.getInt("square");
        apartment.seller = resultSet.getInt("seller");
        apartment.databaseId = resultSet.getInt("id");
        apartment.photos = getPhotosByApartmentId(apartment.databaseId);
        resultSet.close();
        return apartment;
    }

    public void deleteSession(Long chatId) throws SQLException {
        deleteApartmentByChatId(chatId);
        deleteWishByChatId(chatId);
        String deleteSql = "delete from sessions " +
                "where chatId = " + chatId;
        Statement statement = connection.createStatement();
        statement.execute(deleteSql);
    }


    public LinkedList<Wish> getWishesByTelegramId(Integer telegramId) throws SQLException {
        return getWishesByTelegramId(telegramId, true);

    }

    public LinkedList<Wish> getWishesByTelegramId(Integer telegramId, boolean added) throws SQLException {
        LinkedList<Wish> wishes = new LinkedList<>();
        String sql =
                "SELECT * FROM wishes " +
                        "WHERE buyer = " + telegramId;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            if (resultSet.getBoolean("added") != added) {
                continue;
            }
            Wish wish = new Wish();
            wish.street = resultSet.getString("street");
            wish.buyer = resultSet.getInt("buyer");
            wish.databaseId = resultSet.getInt("id");
            wish.price = resultSet.getInt("price");
            wish.square = resultSet.getInt("square");
            wishes.add(wish);
        }
        resultSet.close();
        return wishes;
    }

    public Integer addWish(Wish wish) throws SQLException {
        return addWish(wish, true);
    }

    public Integer addWish(Wish wish, boolean added) throws SQLException {
        String sql =
                "insert into wishes (street, price, square, buyer, added) " +
                        "values (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, wish.street);
        preparedStatement.setInt(2, wish.price);
        preparedStatement.setInt(3, wish.square);
        preparedStatement.setInt(4, wish.buyer);
        preparedStatement.setBoolean(5, added);
        preparedStatement.execute();
        wish.databaseId = preparedStatement.getGeneratedKeys().getInt(1);
        return wish.databaseId;
    }

    public void deleteWishById(Integer wishId) throws SQLException {
        Statement statement = connection.createStatement();
        String sql =
                "delete from wishes " +
                        "where id = " + wishId;
        statement.execute(sql);
    }

    public List<Wish> getAllWishes(Integer id) throws SQLException {
        List<Wish> wishes = new LinkedList<>();
        Statement statement = connection.createStatement();
        String sql =
                "SELECT * FROM wishes " +
                        "WHERE added and buyer <> " + id;
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            Wish wish = new Wish();
            wish.street = resultSet.getString("street");
            wish.buyer = resultSet.getInt("buyer");
            wish.databaseId = resultSet.getInt("id");
            wish.price = resultSet.getInt("price");
            wish.square = resultSet.getInt("square");
            wishes.add(wish);
        }
        resultSet.close();
        return wishes;
    }

    public List<Wish> getSmartWishesBySellerId(Integer sellerId) throws SQLException {
        List<Wish> wishes = new LinkedList<>();
        Statement statement = connection.createStatement();
        String sql =
                "SELECT DISTINCT w.* FROM apartments a, wishes w " +
                        "WHERE  w.street = a.street AND " +
                        "w.buyer <> " + sellerId + " AND " +
                        "w.added AND a.added AND " +
                        "w.price >= a.price AND w.square<=a.price";
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            Wish wish = new Wish();
            wish.street = resultSet.getString("street");
            wish.buyer = resultSet.getInt("buyer");
            wish.databaseId = resultSet.getInt("id");
            wish.price = resultSet.getInt("price");
            wish.square = resultSet.getInt("square");
            wishes.add(wish);
        }
        resultSet.close();
        return wishes;
    }


    public LinkedList<BotUser> getUsersWithApartmentsOnStreet(String street, Integer telegramId) throws SQLException {
        LinkedList<BotUser> users = new LinkedList<>();
        Statement statement = connection.createStatement();

        String sql = "SELECT * FROM users " +
                "WHERE telegramId in (SELECT seller FROM apartments WHERE street = \'" + street + "\' and seller <> " + telegramId + ")";

        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            BotUser botUser = new BotUser();
            botUser.telegramId = resultSet.getInt("telegramId");
            botUser.firstName = resultSet.getString("firstName");
            botUser.lastName = resultSet.getString("lastName");
            botUser.userName = resultSet.getString("userName");
            botUser.email = resultSet.getString("email");
            botUser.phoneNumber = resultSet.getString("phoneNumber");
            users.add(botUser);
        }
        resultSet.close();
        return users;
    }
    public List<Apartment> getSmartApartmentsByBuyerId(Integer buyerId) throws SQLException {
        List<Apartment> apartments = new LinkedList<>();
        Statement statement = connection.createStatement();
        String sql =
                "SELECT DISTINCT a.* FROM apartments a, wishes w " +
                        "WHERE  w.street = a.street AND " +
                        "a.seller <> " + buyerId + " " +
                        "w.added AND a.added AND " +
                        "w.price >= a.price AND w.square<=a.price";
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            Apartment apartment = new Apartment();
            apartment.street = resultSet.getString("street");
            apartment.houseNumber = resultSet.getString("houseNumber");
            apartment.number = resultSet.getInt("number");
            apartment.price = resultSet.getInt("price");
            apartment.square = resultSet.getInt("square");
            apartment.seller = resultSet.getInt("seller");
            apartment.databaseId = resultSet.getInt("id");
            apartments.add(apartment);
        }
        resultSet.close();
        return apartments;
    }
    public List<Apartment> getAllApartmentsByBuyerId(Integer buyerId) throws SQLException {
        List<Apartment> apartments = new LinkedList<>();
        Statement statement = connection.createStatement();
        String sql =
                "SELECT * FROM apartments a " +
                        "WHERE seller <> "+buyerId;
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            Apartment apartment = new Apartment();
            apartment.street = resultSet.getString("street");
            apartment.houseNumber = resultSet.getString("houseNumber");
            apartment.number = resultSet.getInt("number");
            apartment.price = resultSet.getInt("price");
            apartment.square = resultSet.getInt("square");
            apartment.seller = resultSet.getInt("seller");
            apartment.databaseId = resultSet.getInt("id");
            apartment.photos = getPhotosByApartmentId(apartment.databaseId);
            apartments.add(apartment);
        }
        resultSet.close();
        return apartments;
    }

    List<String> getPhotosByApartmentId(Integer apartmentId) throws SQLException {
        List<String> photos = new LinkedList<>();
        String sql = "SELECT * FROM photos " +
                "where apartmentId = " + apartmentId;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            String photo = resultSet.getString("photo");
            photos.add(photo);
        }
        resultSet.close();
        return photos;
    }

    public LinkedList<BotUser> getUsersWithWishesOnStreet(String street, Integer telegramId) throws SQLException {
        LinkedList<BotUser> users = new LinkedList<>();
        Statement statement = connection.createStatement();
        String sql =
                "SELECT * FROM users " +
                        "WHERE telegramId in (SELECT buyer FROM wishes WHERE street = '" + street + "' and buyer <> " + telegramId + ")";
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            BotUser botUser = new BotUser();
            botUser.telegramId = resultSet.getInt("telegramId");
            botUser.firstName = resultSet.getString("firstName");
            botUser.lastName = resultSet.getString("lastName");
            botUser.userName = resultSet.getString("userName");
            botUser.email = resultSet.getString("email");
            botUser.phoneNumber = resultSet.getString("phoneNumber");
            users.add(botUser);
        }
        resultSet.close();
        return users;
    }
}
