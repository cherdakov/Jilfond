package com.jilfond.bot;

import org.telegram.telegrambots.api.methods.send.SendContact;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.updateshandlers.DownloadFileCallback;

import java.sql.SQLException;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    private Manager manager;


    Bot() throws SQLException {
        currentBot = this;
        manager = new Manager(); //manager use static field currentBot!
    }

    @Override
    public void onUpdateReceived(Update update) {

        Long chatId = update.getMessage().getChatId();

        if (update.hasMessage()) {
            manager.pushMessage(update.getMessage());
        }

        /*
        SendPhoto sendPhotoRequest  = new SendPhoto().setChatId(chatId).setPhoto("AgADAgAD8KgxG-U1CUhM-zZxy4E_7JBTqw4ABOpNLn1DAXgG-8UBAAEC");
        try {
            sendPhoto(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        */
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

    private static Bot currentBot;

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