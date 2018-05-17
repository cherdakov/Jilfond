package com.jilfond.bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public class SellerSession extends Session {

    SellerSession(Database database, Long chatId) {
        super(database, chatId);
    }
    @Override
    public SendMessage pushMessage(Message message) {
        Thread thread = new Thread(() -> answer.setText("pushMessageInSellerSession"));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            answer.setText("error");
        }
        return answer;
    }

}
