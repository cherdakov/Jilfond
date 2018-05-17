package com.jilfond.bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;


import java.util.LinkedList;
import java.util.TreeMap;



public class Manager {
    private TreeMap<Long, Session> sessions = new TreeMap<>();
    TelegramLongPollingBot bot;
    Database database = new Database();
    Manager(TelegramLongPollingBot bot) {
        this.bot = bot;
    }
    public SendMessage pushMessage(Message message) {
        Long chatId = message.getChatId();
        if(message.getText().equals("/start")){
            return sendActionKeyboard(chatId);
        }
        if (sessions.containsKey(chatId)) {
            Session session = sessions.get(chatId);
            if(session.getState().equals("SELECTS_AN_ACTION") && message.getText().equals("/cancel")){
                dropSession(chatId);
            } else{
                return session.pushMessage(message);
            }
        } else{
            return createSession(chatId, message.getText());
        }
        return sendActionKeyboard(chatId);
    }

    private SendMessage sendActionKeyboard(Long userId){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        LinkedList<KeyboardRow> keyboardRows = new LinkedList<>();
        KeyboardRow keyboardButtons = new KeyboardRow();
        keyboardButtons.add("Seller");
        keyboardButtons.add("Customer");
        keyboardRows.add(0,keyboardButtons);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return new SendMessage().setChatId(userId).setText("Select role").setReplyMarkup(replyKeyboardMarkup);
    }

    SendMessage createSession(Long chatId, String role){
        if(role.equals("Seller")){
            sessions.put(chatId, new SellerSession(database,chatId));
        } else if(role.equals("Customer")){
            sessions.put(chatId, new CustomerSession(database,chatId));
        }
        return sessions.get(chatId).getFirstActions();
    }
    void dropSession(Long chatId){
        sessions.remove(chatId);
        sendActionKeyboard(chatId);
    }
}
