package com.jilfond.bot;

import com.jilfond.bot.databases.Database;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;


import java.sql.SQLException;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.TreeSet;


public class MessageManager {
    private Database database = new Database();
    private TreeMap<Long, Session> sessions = new TreeMap<Long, Session>();
    private TreeSet<Integer> usersWhoChangingPhoneNumber = new TreeSet<Integer>();
    private TreeSet<Integer> usersWhoChangingEmail = new TreeSet<Integer>();
    Bot bot = Bot.getCurrentBot();
    ReplyKeyboardMarkup selectActionKeyboardMarkup = createSelectActionKeyboard();


    MessageManager() throws SQLException {
    }

    private ReplyKeyboardMarkup createSelectActionKeyboard() {
        LinkedList<String> actions = new LinkedList<>();
        actions.add("Sell");
        actions.add("Buy");
        actions.add("Set phone number");
        actions.add("Set email");
        return Keyboards.make(actions,true);
    }

    public void pushMessage(Message message) {
        Long chatId = message.getChatId();
        Integer userId = message.getFrom().getId();
        if(!message.hasText()){
            sessions.get(chatId).pushMessage(message);
            return;
        }
        if (message.getText().equals("/start")) {
            sendSelectActionRequest(chatId);
            try {
                database.addUserIfNotExist(new BotUser(message.getFrom()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            if (sessions.containsKey(chatId)) {
                Session session = sessions.get(chatId);
                if (session.getState().equals("SELECT_ACTION") && message.getText().equals("Cancel")) {
                    dropSession(chatId);
                    sendSelectActionRequest(chatId);
                } else {
                    session.pushMessage(message);
                }
            } else if(usersWhoChangingPhoneNumber.contains(userId)) {
                if(!message.getText().equals("Cancel")) {
                    try {
                        database.updatePhoneNumber(userId, message.getText());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        //TODO:send error
                    }
                }
                usersWhoChangingPhoneNumber.remove(userId);
                sendSelectActionRequest(chatId);
            } else if(usersWhoChangingEmail.contains(userId)) {
                if(!message.getText().equals("Cancel")) {
                    try {
                        database.updateEmail(userId, message.getText());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        //TODO:send error
                    }
                }
                usersWhoChangingEmail.remove(userId);
                sendSelectActionRequest(chatId);
            } else{
                handleMessageAsAction(message);
            }
        }
    }


    void handleMessageAsAction(Message message) {
        Integer userId = message.getFrom().getId();
        Long chatId = message.getChatId();
        switch (message.getText()) {
            case "Sell":
                sessions.put(chatId, new SellerSession(database, message.getChatId()));
                break;
            case "Buy":
                sessions.put(chatId, new BuyerSession(database, message.getChatId()));
                break;
            case "Set phone number":
                usersWhoChangingPhoneNumber.add(userId);
                bot.send(message.getChatId(),"Send your current phone number, please", Keyboards.onlyCancel);
                break;
            case "Set email":
                usersWhoChangingEmail.add(userId);
                bot.send(message.getChatId(),"Send your current email, please", Keyboards.onlyCancel);
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
