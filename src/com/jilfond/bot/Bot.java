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
        send(update.getMessage().getChatId(),Integer.toString(update.getMessage().getContact().getUserID()),null);
    }

    void send(Long chatId, String text, ReplyKeyboardMarkup keyboard){
        try {
            SendMessage sendMessage = new SendMessage().
                    setChatId(chatId).
                    setText(text);
            if(keyboard!=null){
                sendMessage.setReplyMarkup(keyboard);
            }
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
    void send(Long chatId, String text){
        send(chatId,text,null);
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