package com.jilfond.bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.LinkedList;


public class Session {
    private Database database;
    protected String state = "SELECT_ACTION";
    Bot bot;
    Long chatId;
    SendMessage answer = new SendMessage();
    Thread currentAction;
    private ReplyKeyboardMarkup selectActionKeyboard;
    LinkedList<String> actions = new LinkedList<>();

    public Session(Database database, Bot bot, Long chatId) {
        this.database = database;
        this.bot = bot;
        this.chatId = chatId;
        answer.setChatId(chatId);
        selectActionKeyboard = createSelectActionKeyboard();
        sendSelectActionRequest();
    }
    @Virtual
    private ReplyKeyboardMarkup createSelectActionKeyboard() {

        actions.add("Add");
        actions.add("Show");
        actions.add("Cancel");

        return Utils.makeKeyboard(actions);
    }

    @Virtual
    public void pushMessage(Message message) {

    }

    public String getState() {
        return state;
    }

    void sendSelectActionRequest() {
        bot.sendKeyboard(chatId, "Select Action", selectActionKeyboard);
    }

}