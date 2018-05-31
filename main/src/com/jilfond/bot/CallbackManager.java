package com.jilfond.bot;

import com.jilfond.bot.databases.Database;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

import java.sql.SQLException;

public class CallbackManager {
    Database database;

    public CallbackManager(Database database) {
        this.database = database;
    }

    public void pushUpdate(Update update) {
        System.out.println(update.getCallbackQuery().toString());
        try {
            database.deleteApartmentById(Integer.valueOf(update.getCallbackQuery().getData()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
