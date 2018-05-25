package com.jilfond.bot;

import com.jilfond.bot.objects.Apartment;
import com.jilfond.bot.databases.Database;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;

import java.sql.SQLException;

public class SellerSession extends Session {
    private Apartment apartment = new Apartment();

    public SellerSession(Database database, Bot bot, Long chatId) {
        super(database, bot, chatId);
    }

    @Override
    public void pushMessage(Message message) {
        try {
            currentAction.join();
        } catch (NullPointerException | InterruptedException e) {
            //its normal situation
        }
        currentAction = new Thread(() -> {
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
                    switch (text) {
                        case "Cancel":
                            sendSelectActionRequest();
                            break;
                        case "Back":
                            sendSendStreetRequest();
                            break;
                        default:
                            try {
                                apartment.number = Integer.parseInt(text);
                                sendSendPriceRequest();
                            } catch (NumberFormatException e) {
                                reply("It is not number :( try again");
                            }
                            break;
                    }
                    break;
                case "SEND_PRICE":
                    switch (text) {
                        case "Cancel":
                            sendSelectActionRequest();
                            break;
                        case "Back":
                            sendSendNumberRequest();
                            break;
                        default:
                            try {
                                apartment.price = Integer.parseInt(text);
                                sendConfirmRequest();
                            } catch (NumberFormatException e) {
                                reply("It is not number :( try again");
                            }
                            break;
                    }
                    break;
                case "CONFIRM":
                    switch (text) {
                        case "Yes":
                            try {
                                User user = message.getFrom();
                                if (!database.exist(user.getId())) {
                                    database.addUser(new BotUser(user));
                                }
                                reply("Done!");
                                sendSelectActionRequest();
                            } catch (SQLException e) {
                                e.printStackTrace();
                                reply("Error!");
                            }
                            break;
                        case "Back":
                            sendSendPriceRequest();
                        case "Cancel":
                            sendSelectActionRequest();
                    }
                    break;

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

    private void sendConfirmRequest() {
        reply("Confirm information", Keyboards.yesBackAndCancel);
        reply(apartment.toString());
        state = "CONFIRM";
    }
}
