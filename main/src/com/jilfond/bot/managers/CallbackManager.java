package com.jilfond.bot.managers;

import com.jilfond.bot.Bot;
import com.jilfond.bot.databases.Database;
import com.jilfond.bot.objects.BotUser;
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
            switch (command) {
                case "deleteWish":
                    bot.deleteMessage(message.getMessageId(), message.getChatId());
                    database.deleteWishById(id);
                    break;
                case "deleteApartment":
                    database.deleteApartmentById(id);
                    bot.deleteMessage(message.getMessageId(), message.getChatId());
                    break;
                case "getUser":
                    BotUser user = database.getBotUserByTelegramId(id);
                    String contactDetails = user.getContact();
                    if (!message.hasPhoto()) {
                        bot.updateText(
                                message.getChatId(),
                                message.getMessageId(),
                                message.getText() + "\n" + contactDetails,
                                null);
                    } else {
                        bot.updateCaption(
                                message.getChatId(),
                                message.getMessageId(),
                                message.getCaption() + "\n" + contactDetails,
                                null);
                    }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
