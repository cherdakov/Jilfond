package com.jilfond.bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {
    private Manager manager = new Manager(this);

    Bot() {
        super();
        currentBot = this;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            manager.pushMessage(update.getMessage());
        }
    }

    public void send(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    void sendKeyboard(Long chatId, String description, ReplyKeyboardMarkup keyboard){
        send(new SendMessage().
                setChatId(chatId).
                setText(description).
                setReplyMarkup(keyboard));
    }

    static Bot currentBot;

    public static Bot getCurrentBot() {
        return currentBot;
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