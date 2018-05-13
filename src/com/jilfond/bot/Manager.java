package com.jilfond.bot;

import org.telegram.telegrambots.api.objects.Message;

import java.util.TreeMap;


public class Manager {
    private TreeMap<Long, Session> sessions = new TreeMap<>();
    Database database = new Database();

    public void pushMessage(Message message) {
        Long userId = message.getChatId();
        //if (!sessions.containsKey(userId)) {
            createSession(userId, message.getText());
        //} else{
            sessions.get(userId).setCurrentMessage(message);
        //}
    }
    void createSession(Long userId, String role){
        if(role.equals("/seller")){
            sessions.put(userId, new SellerSession(database));
        } else if(role.equals("/customer")){
            sessions.put(userId, new SellerSession(database));
        }
    }
}
