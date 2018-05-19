package com.jilfond.bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public class SellerSession extends Session {

    public SellerSession(Database database, Bot bot, Long chatId) {
        super(database, bot, chatId);

    }

    @Override
    public void pushMessage(Message message) {
        try {
            currentAction.join();
        } catch (InterruptedException e) {
        } catch (NullPointerException e){
        }
        currentAction = new Thread(new Runnable() {
            @Override
            public void run() {
                String text = message.getText();
                switch (state){
                    case "SELECT ACTION":
                        switch (text){
                            case "Add":
                                break;
                            case "Show":
                                break;
                            case "Cancel":
                                //unreachable because this situation is handled by the manager
                                break;
                        }
                        break;
                }
                bot.send(answer);
            }
        });
        currentAction.start();
    }


}
