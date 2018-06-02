package com.jilfond.bot.managers;

import com.jilfond.bot.Bot;
import com.jilfond.bot.databases.Database;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

import java.sql.SQLException;

public class CallbackManager {
    Database database;
    Bot bot = Bot.getCurrentBot();
    public CallbackManager(Database database) {
        this.database = database;
    }

    public void pushUpdate(Update update) {
        try {
            database.deleteApartmentById(Integer.valueOf(update.getCallbackQuery().getData()));
            Message message = update.getCallbackQuery().getMessage();
            bot.deleteMessage(message.getMessageId(),message.getChatId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
