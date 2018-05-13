package com.jilfond.bot;


import org.telegram.telegrambots.api.objects.Message;

public class SellerSession extends Session {
    enum State {
        SELECT_OF_ACTION,
        WRITE_PRICE,
        WRITE_STREET,
        WRITE_SQRUARE,
        CONFIRM,
        NONE
    };
    private State currentState;
    private Message currentMessage;

    SellerSession(Database database) {
        super(database);
    }

    @Override
    public void setCurrentMessage(Message message) {
        this.currentMessage = message;
        start();//TODO: how to get rid of it?
    }

    @Override
    public void run() {
        for(int i=0; i<5; ++i){
            System.out.println(Integer.toString(i));
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
