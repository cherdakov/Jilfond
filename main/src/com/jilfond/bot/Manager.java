package com.jilfond.bot;

import com.jilfond.bot.databases.Database;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;


import java.sql.SQLException;
import java.util.LinkedList;
import java.util.TreeMap;


public class Manager {
    private Database database;
    private TreeMap<Long, Session> sessions = new TreeMap<>();
    Bot bot;
    ReplyKeyboardMarkup selectActionKeyboardMarkup;


    Manager(Bot bot) throws SQLException {
        this.bot = bot;
        selectActionKeyboardMarkup = createSelectActionKeyboard();
        database = new Database();
    }

    private ReplyKeyboardMarkup createSelectActionKeyboard() {
        LinkedList<String> actions = new LinkedList<>();
        actions.add("Sell");
        actions.add("Buy");
        actions.add("Set phone number");
        actions.add("Set email");
        return Keyboards.make(actions);
    }

    public void pushMessage(Message message) {
        Integer userId = message.getFrom().getId();
        Long chatId = message.getChatId();
        if (message.getText().equals("/start")) {
            sendSelectActionRequest(chatId);
        } else {
            if (sessions.containsKey(chatId)) {
                Session session = sessions.get(chatId);
                if (session.getState().equals("SELECT_ACTION") && message.getText().equals("Cancel")) {
                    dropSession(chatId);
                    sendSelectActionRequest(chatId);
                } else {
                    session.pushMessage(message);
                }
            } else {
                handleMessageAsAction(chatId, message.getText());
            }
        }
    }


    void handleMessageAsAction(Long chatId, String command) {
        switch (command) {
            case "Sell":
                sessions.put(chatId, new SellerSession());
                break;
            case "Buy":
                sessions.put(chatId, new BuyerSession());
                break;
            case "Set phone number":
                break;
            case "Set email":
                break;
        }
    }

    void dropSession(Long chatId) {
        sessions.remove(chatId);
    }

    void sendSelectActionRequest(Long chatId) {
        bot.send(chatId, "Select action", selectActionKeyboardMarkup);
    }

}
