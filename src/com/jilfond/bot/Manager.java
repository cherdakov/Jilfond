package com.jilfond.bot;

import org.telegram.telegrambots.api.objects.Message;

import java.util.Map;

public class Manager {
    private Map<Integer,Session> sessions;
    Database database = new Database();
    public void pushMessage(Message message) {
        Integer userId = message.getContact().getUserID();
        if(!sessions.containsKey(userId)){
            sessions.put(userId,new Session(database));
        }
        sessions.get(userId).pushMessage(message);
    }
}
