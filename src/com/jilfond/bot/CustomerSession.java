package com.jilfond.bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

import java.util.LinkedList;

public class CustomerSession extends Session {
    CustomerSession(Database database, Long chatId) {
        super(database, chatId);
    }

    LinkedList<String> firstActions = new LinkedList<>();

    @Override
    public SendMessage pushMessage(Message message) {
        firstActions.add("Add");
        firstActions.add("Show");

        return new SendMessage().setChatId(chatId).setText("text");
        /*
        Thread thread = new Thread(() -> answer.setText("from  SellerSession"));
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return answer;
        */
    }

}
