package com.jilfond.bot;

import com.jilfond.bot.databases.Database;
import com.jilfond.bot.managers.CallbackManager;
import com.jilfond.bot.managers.MessageManager;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.*;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

public class Bot extends TelegramLongPollingBot {
    private MessageManager messageManager;
    private CallbackManager callbackManager;
    private Database database = new Database();
    private static Bot currentBot;


    public Bot() throws SQLException {
        currentBot = this;
        messageManager = new MessageManager(database); //messageManager use static field currentBot!
        callbackManager = new CallbackManager(database); //messageManager use static field currentBot!
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage()){
            try {
                messageManager.pushMessage(update.getMessage());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if(update.hasCallbackQuery()){
            callbackManager.pushUpdate(update);
        }
    }


    public void send(Long chatId, String text, ReplyKeyboard keyboard){
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


    public void send(Long chatId, String text){
        send(chatId,text,null);
    }


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

    public void sendPicture(Long chatId, String photo, String description, ReplyKeyboard replyKeyboard) {
        SendPhoto sendPhotoRequest = new
                SendPhoto().
                setChatId(chatId).
                setPhoto(photo).
                setCaption(description);
        if (replyKeyboard!=null){
            sendPhotoRequest.setReplyMarkup(replyKeyboard);
        }
        try {
            sendPhoto(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void deleteMessage(Integer messageId, Long chatId){
        DeleteMessage deleteMessage = new DeleteMessage().setMessageId(messageId).setChatId(chatId);
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void updateText(Long chatId, Integer messageId, String text, InlineKeyboardMarkup replyMarkup){
        EditMessageText editMessageText = new EditMessageText()
                .setChatId(chatId)
                .setMessageId(messageId)
                .setText(text);
        if(replyMarkup!=null){
            editMessageText.setReplyMarkup(replyMarkup);
        }
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void updateCaption(Long chatId, Integer messageId, String text, InlineKeyboardMarkup replyMarkup){
        EditMessageCaption editMessageCaption = new EditMessageCaption()
                .setChatId(String.valueOf(chatId))
                .setMessageId(messageId)
                .setCaption(text);
        if(replyMarkup!=null){
            editMessageCaption.setReplyMarkup(replyMarkup);
        }
        try {
            execute(editMessageCaption);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}