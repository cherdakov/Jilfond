package com.jilfond.bot.tests;

import com.jilfond.bot.Bot;
import com.jilfond.bot.objects.BotUser;
import com.jilfond.bot.databases.Database;
import com.jilfond.bot.objects.Apartment;
import com.jilfond.bot.sessions.SellerSession;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {
    @Test
    void addGetExistRemoveUser() {
        BotUser botUser = new BotUser();
        botUser.phoneNumber = "+79131112233";
        botUser.email = "email@gmail.com";
        botUser.userName = "AlexDarkStalker98";
        botUser.firstName = "Иван";
        botUser.lastName = "Петров";
        botUser.telegramId = 123454321;
        try {
            Database database = new Database();
            //database.deleteUserByTelegramId(botUser.telegramId);
            database.addUser(botUser);
            assertEquals(true, database.userExist(botUser.telegramId));
            assertEquals(botUser.toString(), database.getBotUserByTelegramId(botUser.telegramId).toString());
            database.deleteUserByTelegramId(botUser.telegramId);
            assertEquals(false, database.userExist(botUser.telegramId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void addGetExistRemoveApartment() {
        Apartment apartment = new Apartment();
        apartment.street = "Chekhov";
        apartment.houseNumber = "12/3";
        apartment.number = 488;
        apartment.square = 35;
        apartment.price = 15000;
        apartment.seller = 123454321;
        try {
            Database database = new Database();
            database.addApartment(apartment);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void updatePhoneNumberAndEmail() {
        BotUser botUser = new BotUser();
        botUser.phoneNumber = "+79131112233";
        botUser.email = "email@gmail.com";
        botUser.userName = "AlexDarkStalker98";
        botUser.firstName = "Иван";
        botUser.lastName = "Петров";
        botUser.telegramId = 123454321;
        try {
            String newPhoneNumber = "+79130001122";
            String newEmail = "yandex@yandex.ru";
            Database database = new Database();
            database.addUserIfNotExist(botUser);
            database.updatePhoneNumber(botUser.telegramId, newPhoneNumber);
            database.updateEmail(botUser.telegramId, newEmail);
            BotUser databaseBotUser = database.getBotUserByTelegramId(botUser.telegramId);
            assertEquals(newPhoneNumber, databaseBotUser.phoneNumber);
            assertEquals(newEmail, databaseBotUser.email);
            database.deleteUserByTelegramId(botUser.telegramId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Test
    void addApartmentTest() {
        Apartment apartment = new Apartment();
        try {
            Database database = new Database();
            database.addApartment(apartment);
            database.deleteApartmentById(apartment.databaseId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getApartmentsByTelegramIdTest() {
        Integer telegramId = 63059291;
        try {
            Database database = new Database();
            LinkedList<Apartment> apartments = database.getApartmentsByTelegramId(telegramId);
            for (Apartment apartment : apartments) {
                System.out.println(apartment.toString());
                System.out.println("photos:");
                for (String photo : apartment.photos) {
                    System.out.println(photo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void saveAndLoadSessionTest() {
        Long chatId = Long.valueOf(123456);
        Database database = null;
        SellerSession sellerSession = null;
        try {
            Bot bot = new Bot();
            database = new Database();
            sellerSession = new SellerSession(database, chatId);
            sellerSession.save();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            assertEquals(true, database.sessionExist(chatId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        SellerSession loadedSellerSession = new SellerSession(database, chatId);
        loadedSellerSession.load();
        assert (sellerSession.equals(loadedSellerSession));
    }
}