package com.jilfond.bot.sessions;

public class SessionDescription {
    public Object object;
    public String state;
    public String action;
    public String type;
    public Long chatId;

    @Override
    public String toString() {
        return "SessionDescription{" +
                "object=" + object +
                ", state='" + state + '\'' +
                ", action='" + action + '\'' +
                ", type='" + type + '\'' +
                ", chatId=" + chatId +
                '}';
    }
}
