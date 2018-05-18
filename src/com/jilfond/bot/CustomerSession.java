package com.jilfond.bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

import java.util.LinkedList;

public class CustomerSession extends Session {

    public CustomerSession(Database database, Bot bot, Long chatId) {
        super(database, bot, chatId);
    }




}
