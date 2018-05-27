package com.jilfond.bot;

import com.jilfond.bot.objects.Apartment;
import com.jilfond.bot.databases.Database;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;

import java.sql.SQLException;

public class SellerSession extends Session {
    private Apartment apartment = new Apartment();

    public SellerSession(Database database, Long chatId) {
        super(database, chatId);
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
                        apartment.setStreet(text);
                        sendSendHouseNumberRequest();
                    }
                    break;
                case "SEND_HOUSE_NUMBER":
                    switch (text) {
                        case "Cancel":
                            sendSelectActionRequest();
                            break;
                        case "Back":
                            sendSendStreetRequest();
                            break;
                        default:
                            try {
                                apartment.houseNumber = text;
                                sendSendApartmentNumberRequest();
                            } catch (NumberFormatException e) {
                                reply("It is not number :( try again");
                            }
                            break;
                    }
                    break;
                case "SEND_APARTMENT_NUMBER":
                    switch (text) {
                        case "Cancel":
                            sendSelectActionRequest();
                            break;
                        case "Back":
                            sendSendHouseNumberRequest();
                            break;
                        default:
                            try {
                                apartment.number = Integer.valueOf(text);
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
                            sendSendApartmentNumberRequest();
                            break;
                        default:
                            try {
                                apartment.price = Integer.parseInt(text);
                                sendSendSquareRequest();
                            } catch (NumberFormatException e) {
                                reply("It is not number :( try again");
                            }
                            break;
                    }
                    break;
                case "SEND_SQUARE":
                    switch (text) {
                        case "Cancel":
                            sendSelectActionRequest();
                            break;
                        case "Back":
                            sendSendPriceRequest();
                            break;
                        default:
                            try {
                                apartment.square = Integer.parseInt(text);
                                apartment.seller = message.getFrom().getId();
                                sendAddPicturesRequest();
                            } catch (NumberFormatException e) {
                                reply("It is not number :( try again");
                            }
                            break;
                    }
                    break;
                case "ADD_PICTURES":
                    if(message.hasPhoto()){
                        apartment.setPhotos(message.getPhoto());
                        System.out.println("REALLY? ");
                        sendConfirmRequest();
                    } else{
                        switch (text) {
                            case "Cancel":
                                sendSelectActionRequest();
                                break;
                            case "Back":
                                sendSendSquareRequest();
                                break;
                            case "No":
                                sendConfirmRequest();
                                break;
                        }
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
                                database.addApartment(apartment);
                                reply("Done!");
                                sendSelectActionRequest();
                            } catch (SQLException e) {
                                e.printStackTrace();
                                reply("Error!");
                            }
                            break;
                        case "Back":
                            sendSendSquareRequest();
                            break;
                        case "Cancel":
                            sendSelectActionRequest();
                            break;
                    }
                    break;
                default:
                    throw new IllegalStateException();
            }
        });
        currentAction.start();
    }

    private void sendAddPicturesRequest() {
        reply("Send me pictures, please", Keyboards.backCancelAndNo);
        state = "ADD_PICTURES";
    }

    private void sendSendSquareRequest() {
        reply("Send me square, please", Keyboards.backAndCancel);
        state = "SEND_SQUARE";
    }


    private void sendSendPriceRequest() {
        reply("Send me price, please", Keyboards.backAndCancel);
        state = "SEND_PRICE";
    }

    private void sendSendStreetRequest() {
        reply("Send me street, please", Keyboards.onlyCancel);
        state = "SEND_STREET";
    }

    private void sendSendHouseNumberRequest() {
        reply("Send me number of house, please", Keyboards.backAndCancel);
        state = "SEND_HOUSE_NUMBER";
    }
    private void sendSendApartmentNumberRequest() {
        reply("Send me number of apartment, please", Keyboards.backAndCancel);
        state = "SEND_APARTMENT_NUMBER";
    }

    private void sendConfirmRequest() {
        reply("Confirm information", Keyboards.yesBackAndCancel);
        reply(apartment.toString());
        state = "CONFIRM";
    }
}
