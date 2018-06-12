package com.jilfond.bot.managers;

import com.jilfond.bot.Bot;
import com.jilfond.bot.objects.BotUser;
import com.jilfond.bot.Keyboards;
import com.jilfond.bot.databases.Database;
import com.jilfond.bot.sessions.BuyerSession;
import com.jilfond.bot.sessions.SellerSession;
import com.jilfond.bot.sessions.Session;
import com.jilfond.bot.sessions.SessionDescription;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;


import java.sql.SQLException;
import java.util.*;


public class MessageManager {
    private Database database;
    private Map<Long, Session> sessions = new HashMap<>();
    private Set<Integer> usersWhoChangingPhoneNumber = new HashSet<Integer>();
    private Set<Integer> usersWhoChangingEmail = new HashSet<Integer>();


    Bot bot = Bot.getCurrentBot();
    ReplyKeyboardMarkup selectActionKeyboardMarkup = createSelectActionKeyboard();
    private final String mutex = "mutex";

    public MessageManager(Database database) {
        this.database = database;
        //TODO:fix this shit!
        Thread activityObserverThread = new Thread(() -> {
            System.out.println("activityObserverThread start");
            while (true) {
                try {
                    Thread.sleep(1000 * 60 * 15);
                    Date currentTime = Calendar.getInstance().getTime();
                    synchronized (mutex) {
                        for (Long key : sessions.keySet()) {
                            Session session = sessions.get(key);
                            if (currentTime.getTime() - session.getLastActivityTime().getTime() > 1000 * 60 * 60) {

                                System.out.println("activityObserverThread:");
                                System.out.println("remove " + key);
                                try {
                                    session.save();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                sessions.remove(key);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        activityObserverThread.start();
    }


    private ReplyKeyboardMarkup createSelectActionKeyboard() {
        LinkedList<String> actions = new LinkedList<>();
        actions.add("Sell");
        actions.add("Buy");
        actions.add("Set phone number");
        actions.add("Set email");
        return Keyboards.make(actions, true);
    }

    public void pushMessage(Message message) throws SQLException {
        Long chatId = message.getChatId();
        Integer userId = message.getFrom().getId();


        if (message.hasText() && message.getText().equals("/start")) {
            database.deleteSession(chatId);
            sessions.remove(chatId);
            sendSelectActionRequest(chatId);
            try {
                database.addUserIfNotExist(new BotUser(message.getFrom()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            synchronized (mutex) {
                if (!sessions.containsKey(chatId) && database.sessionExist(chatId)) {//TODO:can be optimized
                    SessionDescription sessionDescription = database.getSession(chatId);
                    switch (sessionDescription.type) {
                        case "SELLER":
                            sessions.put(chatId, new SellerSession(database, sessionDescription));
                            break;
                        case "BUYER":
                            sessions.put(chatId, new BuyerSession(database, sessionDescription));
                            break;
                    }
                }
                if (sessions.containsKey(chatId)) {
                    Session session = sessions.get(chatId);
                    if (session.getState().equals("SELECT_ACTION") && message.getText().equals("Cancel")) {
                        sessions.remove(chatId);
                        database.deleteSession(chatId);
                        sendSelectActionRequest(chatId);
                    } else {
                        session.pushMessage(message);
                    }
                } else if (usersWhoChangingPhoneNumber.contains(userId)) {
                    if (!message.getText().equals("Cancel")) {
                        try {
                            database.updatePhoneNumber(userId, message.getText());
                        } catch (SQLException e) {
                            e.printStackTrace();
                            bot.send(chatId, "Error");
                        }
                    }
                    usersWhoChangingPhoneNumber.remove(userId);
                    sendSelectActionRequest(chatId);
                } else if (usersWhoChangingEmail.contains(userId)) {
                    if (!message.getText().equals("Cancel")) {
                        try {
                            database.updateEmail(userId, message.getText());
                        } catch (SQLException e) {
                            e.printStackTrace();
                            bot.send(chatId, "Error");
                        }
                    }
                    usersWhoChangingEmail.remove(userId);
                    sendSelectActionRequest(chatId);
                } else {
                    handleMessageAsAction(message);
                }
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
                bot.send(message.getChatId(), "Send your current phone number, please", Keyboards.cancel);
                break;
            case "Set email":
                usersWhoChangingEmail.add(userId);
                bot.send(message.getChatId(), "Send your current email, please", Keyboards.cancel);
                break;
            default:
                bot.send(message.getChatId(), "to start, send me /start", Keyboards.start);
        }
    }

    void sendSelectActionRequest(Long chatId) {
        bot.send(chatId, "Select action", selectActionKeyboardMarkup);
    }

}
