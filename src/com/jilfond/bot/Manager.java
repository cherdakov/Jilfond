package com.jilfond.bot;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;


import java.util.LinkedList;
import java.util.TreeMap;


public class Manager {
    private TreeMap<Long, Session> sessions = new TreeMap<>();
    Bot bot;
    Database database = new Database();
    ReplyKeyboardMarkup selectRoleKeyboardMarkup;


    Manager(Bot bot) {
        this.bot = bot;
        selectRoleKeyboardMarkup = createSelectRoleKeyboard();

    }

    private ReplyKeyboardMarkup createSelectRoleKeyboard() {
        LinkedList<String> roles = new LinkedList<>();
        roles.add("Seller");
        roles.add("Customer");
        return Keyboards.make(roles);
    }

    public void pushMessage(Message message) {
        Long chatId = message.getChatId();
        if (message.getText().equals("/start")) {
            sendSelectRoleRequest(chatId);
        } else {
            if (sessions.containsKey(chatId)) {
                Session session = sessions.get(chatId);
                if (session.getState().equals("SELECT_ACTION") && message.getText().equals("Cancel")) {
                    dropSession(chatId);
                    sendSelectRoleRequest(chatId);
                } else {
                    session.pushMessage(message);
                }
            } else {
                createSession(chatId, message.getText());
            }
        }
    }


    void createSession(Long chatId, String role) {
        if (role.equals("Seller")) {
            sessions.put(chatId, new SellerSession(database, bot, chatId));
        } else if (role.equals("Customer")) {
            sessions.put(chatId, new CustomerSession(database, bot, chatId));
        }
    }

    void dropSession(Long chatId) {
        sessions.remove(chatId);
    }

    void sendSelectRoleRequest(Long chatId) {
        bot.send(chatId, "Select Role", selectRoleKeyboardMarkup);
    }

}
