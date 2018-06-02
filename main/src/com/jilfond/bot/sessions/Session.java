package com.jilfond.bot.sessions;

import com.jilfond.bot.Bot;
import com.jilfond.bot.Keyboards;
import com.jilfond.bot.annotations.Virtual;
import com.jilfond.bot.databases.Database;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.LinkedList;


public class Session {
    protected Database database;
    protected String state = "SELECT_ACTION";
    protected Bot bot;
    protected Long chatId;
    protected Thread currentThreadAction;
    protected LinkedList<String> actions = new LinkedList<>();
    protected ReplyKeyboardMarkup selectActionKeyboard = createSelectActionKeyboard();

    protected String type;

    public Session(Database database, Long chatId) {
        this.bot = Bot.getCurrentBot();
        this.chatId = chatId;
        this.database = database;
        sendSelectActionRequest();
    }

    void reply(String text, ReplyKeyboardMarkup keyboard) {
        bot.send(chatId,text,keyboard);
    }

    void reply(String text, InlineKeyboardMarkup keyboard) {
        bot.send(chatId,text,keyboard);
    }

    void reply(String text) {
        bot.send(chatId,text);
    }
    void replyWithPhoto(String photo, String description){
        bot.sendPicture(this.chatId, photo, description);
    }

    @Virtual
    private ReplyKeyboardMarkup createSelectActionKeyboard() {
        actions.add("Add");
        actions.add("Show Apartments");
        actions.add("Show Wishes");
        actions.add("Delete");
        actions.add("Cancel");
        return Keyboards.make(actions,true);
    }

    @Virtual
    public void pushMessage(Message message) {

    }

    public String getState() {
        return state;
    }

    void sendSelectActionRequest() {
        state = "SELECT_ACTION";
        bot.send(chatId, "Select ACTION", selectActionKeyboard);
    }

}