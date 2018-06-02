package com.jilfond.bot;

import com.jilfond.bot.databases.Database;
import com.jilfond.bot.managers.CallbackManager;
import com.jilfond.bot.managers.MessageManager;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.*;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

public class Bot extends TelegramLongPollingBot {
    private MessageManager messageManager;
    private CallbackManager callbackManager;
    private Database database = new Database();
    private static Bot currentBot;


    Bot() throws SQLException {
        currentBot = this;
        messageManager = new MessageManager(database); //messageManager use static field currentBot!
        callbackManager = new CallbackManager(database); //messageManager use static field currentBot!
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage()){
            messageManager.pushMessage(update.getMessage());
        } else if(update.hasCallbackQuery()){
            callbackManager.pushUpdate(update);
        }

        /*
        if (update.hasMessage()) {
            //messageManager.pushMessage(update.getMessage());
            SendMessage sendMessage = new SendMessage().setText("no, please!").setChatId(update.getMessage().getChatId());
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> listOfButtonsList = new LinkedList<>();
            List<InlineKeyboardButton> buttons = new LinkedList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText("DELETE THIS!");
            inlineKeyboardButton.setCallbackData("DELETE THIS MESSAGE");
            buttons.add(inlineKeyboardButton);
            listOfButtonsList.add(buttons);
            inlineKeyboardMarkup.setKeyboard(listOfButtonsList);
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if(update.hasCallbackQuery()){
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chatId).setMessageId(messageId);
            try {
                execute(deleteMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else{

        }
        */

    }


    public void send(Long chatId, String text, ReplyKeyboardMarkup keyboard){
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

    public void send(Long chatId, String text, InlineKeyboardMarkup keyboard){
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
        try {
            SendMessage sendMessage = new SendMessage().
                    setChatId(chatId).
                    setText(text);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
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

    public void sendPicture(Long chatId, String photo, String description) {
        SendPhoto sendPhotoRequest = new SendPhoto().setChatId(chatId).setPhoto(photo).setCaption(description);
        try {
            sendPhoto(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}