package com.jilfond.bot.tests;

import com.jilfond.bot.BotUser;
import com.jilfond.bot.databases.Database;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {

    @Test
    void addGetExistRemove() {
        BotUser botUser = new BotUser();
        botUser.phoneNumber="+79131112233";
        botUser.email="email@gmail.com";
        botUser.userName="AlexDarkStalker98";
        botUser.firstName="Иван";
        botUser.lastName="Петров";
        botUser.telegramId = 123454321;
        try {
            Database database = new Database();
            //database.deleteUserByTelegramId(botUser.telegramId);
            database.addUser(botUser);
            assertEquals(true,database.exist(botUser.telegramId));
            botUser.databaseId=database.getBotUserByTelegramId(botUser.telegramId).databaseId;
            assertEquals(botUser.toString(),database.getBotUserByTelegramId(botUser.telegramId).toString());
            database.deleteUserByTelegramId(botUser.telegramId);
            assertEquals(false,database.exist(botUser.telegramId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}