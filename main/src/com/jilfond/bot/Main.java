package com.jilfond.bot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            Bot bot = new Bot();
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException | SQLException e) {
            e.printStackTrace();
        }

    }
}
