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
        String callback = update.getCallbackQuery().getData();
        String command = callback.split(" ")[0];
        Integer id = Integer.valueOf(callback.split(" ")[1]);
        Message message = update.getCallbackQuery().getMessage();
        try {
        switch (command){
            case "deleteWish":
                bot.deleteMessage(message.getMessageId(),message.getChatId());
                database.deleteWishById(id);
                break;
            case "deleteApartment":
                database.deleteApartmentById(id);
                bot.deleteMessage(message.getMessageId(),message.getChatId());
                break;

        }
        } catch (SQLException e) {
            e.printStackTrace();
        }




    }
}
