package com.jilfond.bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {
    private Manager manager = new Manager();
    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();
        System.out.println(chatId);
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            manager.pushMessage(update.getMessage());
            SendMessage sendMessage = new SendMessage().setText(Long.toString(chatId)).setChatId(update.getMessage().getChatId());
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onClosing() {
        System.out.println("onClosing");
        super.onClosing();
    }

    @Override
    public String getBotUsername() {
        return "JilfondBot";
    }

    @Override
    public String getBotToken() {
        return "522474427:AAHsCXHRTz4UYhOQovlGQdNheAA2qBQh-rY";
}
}