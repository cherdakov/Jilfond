package com.jilfond.bot;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class SellerSession extends Session {
    private Apartment apartment = new Apartment();
    public SellerSession(Database database, Bot bot, Long chatId) {
        super(database, bot, chatId);
    }

    @Override
    public void pushMessage(Message message) {
        try {
            currentAction.join();
        } catch (InterruptedException e) {
        } catch (NullPointerException e) {
        }
        currentAction = new Thread(new Runnable() {
            @Override
            public void run() {
                String text = message.getText();

                switch (state) {
                    case "SELECT_ACTION":
                        switch (text) {
                            case "Add":
                                sendSendStreetRequest();
                                break;
                            case "Show":
                                break;
                            case "Cancel":
                                //unreachable because this situation is handled by the manager
                                break;
                        }
                        break;
                    case "SEND_STREET":
                        if (text.equals("Cancel")) {
                            sendSelectActionRequest();
                        } else {
                            apartment.street = text;
                            sendSendNumberRequest();
                        }
                        break;
                    case "SEND_NUMBER":
                        if (text.equals("Cancel")) {
                            sendSelectActionRequest();
                        } else if (text.equals("Back")) {
                            sendSendStreetRequest();
                        } else {
                            try {
                                apartment.number = Integer.parseInt(text);
                                sendSendPriceRequest();
                            } catch (NumberFormatException e){
                                reply("It is not number :( try again");
                            }

                        }
                        break;
                    case "SEND_PRICE":
                        if (text.equals("Cancel")) {
                            sendSelectActionRequest();
                        } else if (text.equals("Back")) {
                            sendSendNumberRequest();
                        } else {
                            try {
                                apartment.price = Integer.parseInt(text);
                                reply("Done!");
                                sendSelectActionRequest();
                            } catch (NumberFormatException e){
                                reply("It is not number :( try again");
                            }
                        }
                        break;
                    case "CONFIRM":
                }
            }
        });
        currentAction.start();
    }

    private void sendSendPriceRequest() {
        reply("Send me price, please", Keyboards.backAndCancel);
        state = "SEND_PRICE";
    }

    private void sendSendStreetRequest() {
        reply("Send me street, please", Keyboards.onlyCancel);
        state = "SEND_STREET";
    }

    private void sendSendNumberRequest() {
        reply("Send me number of house, please", Keyboards.backAndCancel);
        state = "SEND_NUMBER";
    }
}
