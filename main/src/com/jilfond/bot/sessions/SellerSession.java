package com.jilfond.bot.sessions;

import com.jilfond.bot.objects.BotUser;
import com.jilfond.bot.Keyboards;
import com.jilfond.bot.objects.Apartment;
import com.jilfond.bot.databases.Database;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.sql.SQLException;
import java.util.LinkedList;

public class SellerSession extends Session {
    private Apartment apartment = new Apartment();
    @Override
    protected Object getObject() {
        return apartment;
    }
    @Override
    protected void setObject(Object object) {
        apartment = (Apartment) object;
    }

    public SellerSession(Database database, SessionDescription sessionDescription) {
        super(database, sessionDescription);
    }

    public SellerSession(Database database, Long chatId) {
        super(database, chatId);
        type = "SELLER";
    }

    @Override
    public void pushMessage(Message message) {
        super.pushMessage(message);//for same logic for all sessions
        System.out.println(message.toString());
        try {
            currentThreadAction.join();
        } catch (NullPointerException | InterruptedException e) {
            //its normal situation
        }
        currentThreadAction = new Thread(() -> {
            switch (action) {
                case "NONE":
                    switch (message.getText()) {
                        case "Add":
                            action = "ADD_APARTMENT";
                            sendSendStreetRequest();
                            break;
                        case "Show Apartments":
                            try {
                                sendApartmentsToSeller(message.getFrom().getId());
                            } catch (SQLException e) {
                                reply("Error!");
                                e.printStackTrace();
                            }
                            break;
                        case "Cancel":
                            //unreachable because this situation is handled by the manager
                            break;
                    }
                    break;
                case "ADD_APARTMENT":
                    handleAddAction(message);
                    break;
                case "SHOW_APARTMENTS":
                    handleShowApartmentsAction(message);
                    break;
                case "SHOW_WISHES":
                    //handleShowWishAction(message);
                    break;
                default:
                    throw new IllegalStateException();
            }
        });
        currentThreadAction.start();
    }

    private void sendApartmentsToSeller(Integer id) throws SQLException {
        LinkedList<Apartment> apartments = database.getApartmentsByTelegramId(id);
        if (apartments.isEmpty()) {
            reply("No added apartments.");
        }
        for (Apartment apartment : apartments) {
            InlineKeyboardMarkup deleteApartmentKeyboard =
                    Keyboards.makeOneButtonInlineKeyboardMarkup("Delete Apartment", String.valueOf(apartment.databaseId));
            if (apartment.photos.isEmpty()) {
                reply(apartment.getDescriptionForSeller(), deleteApartmentKeyboard);
            } else {
                replyWithPhoto(apartment.photos.get(0), apartment.getDescriptionForSeller(), deleteApartmentKeyboard);
            }
        }
    }

    private void handleShowApartmentsAction(Message message) {

    }

    private void handleAddAction(Message message) {
        String text = message.getText();
        switch (state) {
            case "SEND_STREET":
                if (text.equals("Cancel")) {
                    sendSelectActionRequest();
                    action = "NONE";
                } else {
                    apartment.street = text;
                    sendSendHouseNumberRequest();
                }
                break;
            case "SEND_HOUSE_NUMBER":
                switch (text) {
                    case "Cancel":
                        action = "NONE";
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
                        action = "NONE";
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
                        action = "NONE";
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
                        action = "NONE";
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
            case "SEND_PICTURE":
                if (message.hasPhoto()) {
                    apartment.addPhoto(message.getPhoto().get(3).getFileId());
                    sendConfirmRequest();
                } else {
                    switch (text) {
                        case "Cancel":
                            action = "NONE";
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
                            database.addApartment(apartment);
                            reply("Done!");
                            sendSelectActionRequest();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            reply("Error!");
                        }
                        action = "NONE";
                        break;
                    case "Back":
                        sendAddPicturesRequest();
                        apartment.photos.clear();
                        break;
                    case "Cancel":
                        action = "NONE";
                        apartment.photos.clear();
                        sendSelectActionRequest();
                        break;
                }
                break;
            default:
                System.out.println(state);
                System.out.println(message.toString());
                throw new IllegalStateException();
        }
    }

    private void sendAddPicturesRequest() {
        reply("Send me pictures, please", Keyboards.backCancelAndNo);
        state = "SEND_PICTURE";
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
