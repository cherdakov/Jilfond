package com.jilfond.bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {
    private Manager manager = new Manager(this);

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();
        SendMessage answer = new SendMessage().setChatId(chatId).setText("error");
        if (update.hasMessage() && update.getMessage().hasText()) {
            answer = manager.pushMessage(update.getMessage());
        }
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClosing() {
        System.out.println("onClosing");
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