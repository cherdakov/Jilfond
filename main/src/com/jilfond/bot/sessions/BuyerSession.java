package com.jilfond.bot.sessions;

import com.jilfond.bot.databases.Database;

public class BuyerSession extends Session {

    public BuyerSession(Database database, Long chatId) {
        super(database, chatId);
    }
}
