package com.jilfond.bot;

import org.telegram.telegrambots.api.objects.Message;

public class Session extends Thread {
    Database database;
    User user;

    Session(Database database) {
        this.database = database;
    }

    public void setCurrentMessage(Message message) {
        //pure virtual ¯\_(ツ)_/¯
    }
}