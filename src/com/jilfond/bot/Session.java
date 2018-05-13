package com.jilfond.bot;

import org.telegram.telegrambots.api.objects.Message;

public class Session extends Thread {
    Database database;
    User user;
    String state;

    Session(Database database) {
        this.database = database;
        run();
    }

    @Override
    public void run() {
        super.run();
        while (true) {

        }
    }

    public void pushMessage(Message message) {
        switch (state) {
            case "shit":
                break;
            default:
                break;
        }
    }
}
