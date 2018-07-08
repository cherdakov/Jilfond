package com.jilfond.bot.sessions;

import com.jilfond.bot.Bot;
import com.jilfond.bot.Keyboards;
import com.jilfond.bot.annotations.Virtual;
import com.jilfond.bot.databases.Database;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Session {
    protected Database database;
    protected String state = "SELECT_ACTION";
    protected String action = "NONE";
    protected Bot bot;
    protected Long chatId;
    protected Message currentMessage;
    protected Thread currentThreadAction;
    protected LinkedList<String> actions = new LinkedList<>();
    protected ReplyKeyboardMarkup selectActionKeyboard = createSelectActionKeyboard();
    protected Runnable runnable;
    private ConcurrentLinkedQueue<Message> messages = new ConcurrentLinkedQueue<>();
    protected String type;
    private Date lastActivityTime = Calendar.getInstance().getTime();
    private Thread messageHandlerThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                Message message = messages.poll();
                if (message != null) {
                    currentMessage = message;
                    try {
                        currentThreadAction.join();
                    } catch (NullPointerException | InterruptedException e) {
                        //its normal situation
                    }
                    currentThreadAction = new Thread(runnable);
                    currentThreadAction.start();
                }
            }
        }
    });

    public Session(Database database, Long chatId) {
        this.bot = Bot.getCurrentBot();
        this.chatId = chatId;
        this.database = database;
        sendSelectActionRequest();
        messageHandlerThread.start();
    }

    public Session(Database database, SessionDescription sessionDescription) {
        this.bot = Bot.getCurrentBot();
        this.chatId = sessionDescription.chatId;
        this.type = sessionDescription.type;
        this.action = sessionDescription.action;
        this.state = sessionDescription.state;
        this.database = database;
        setObject(sessionDescription.object);
        messageHandlerThread.start();
    }

    void reply(String text, ReplyKeyboard replyKeyboard) {
        bot.send(chatId, text, replyKeyboard);
    }

    void reply(String text) {
        bot.send(chatId, text, null);
    }

    void replyWithPhoto(String photo, String description, InlineKeyboardMarkup deleteApartmentKeyboard) {
        bot.sendPicture(this.chatId, photo, description, deleteApartmentKeyboard);
    }

    @Virtual
    private ReplyKeyboardMarkup createSelectActionKeyboard() {
        actions.add("Add");
        actions.add("Show Apartments");
        actions.add("Show Wishes");
        actions.add("Cancel");
        return Keyboards.make(actions, true);
    }

    public void pushMessage(Message message) {
        messages.offer(message);
        lastActivityTime = Calendar.getInstance().getTime();
    }


    public String getState() {
        return state;
    }

    public void sendSelectActionRequest() {
        action = "NONE";
        state = "SELECT_ACTION";
        bot.send(chatId, "Select Action", selectActionKeyboard);
    }

    public void save() throws SQLException {
        SessionDescription sessionDescription = new SessionDescription();
        sessionDescription.object = this.getObject();
        sessionDescription.state = this.state;
        sessionDescription.action = this.action;
        sessionDescription.type = this.type;
        sessionDescription.chatId = this.chatId;
        database.saveSession(sessionDescription);
    }

    public void load() {
        try {
            if (database.sessionExist(chatId)) {
                SessionDescription sessionDescription = null;
                try {
                    sessionDescription = database.getSession(chatId);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                setObject(sessionDescription.object);
                this.state = sessionDescription.state;
                this.action = sessionDescription.action;
                this.type = sessionDescription.type;
                this.chatId = sessionDescription.chatId;
                System.out.println("loaded session:\n" + sessionDescription.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Virtual
    protected void setObject(Object object) {
        System.out.println("NO!");
    }

    @Virtual
    protected Object getObject() {
        System.out.println("FUCK?");
        return null;
    }

    public Date getLastActivityTime() {
        return lastActivityTime;
    }
}