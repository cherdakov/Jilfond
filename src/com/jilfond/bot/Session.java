package com.jilfond.bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

import java.util.LinkedList;

public class Session {
    Database database;
    String state = "SELECTS_AN_ACTION";;
    Long chatId;
    SendMessage answer = new SendMessage();
    protected Message currentMessage;

    Session(Database database, Long chatId) {
        this.database = database;
        this.chatId = chatId;
        answer.setChatId(chatId);
    }

    public SendMessage pushMessage(Message message) {
        //pure virtual ¯\_(ツ)_/¯;
        return new SendMessage().setChatId(chatId).setText("pushMessage");
    }

    public String getState() {
        return state;
    }

    public SendMessage getFirstActions() {
        //pure virtual ¯\_(ツ)_/¯;
        return new SendMessage().
                setChatId(chatId).
                setText("Select action").setReplyMarkup(Utils.makeKeyboard());
    }

}