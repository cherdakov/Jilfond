package com.jilfond.bot.sessions;

import com.jilfond.bot.Keyboards;
import com.jilfond.bot.managers.Notifier;
import com.jilfond.bot.objects.Apartment;
import com.jilfond.bot.databases.Database;
import com.jilfond.bot.objects.Wish;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class SellerSession extends Session {
    private Apartment apartment;
    private Runnable sellerRunnable = () -> {
        switch (action) {
            case "NONE":
                switch (currentMessage.getText()) {
                    case "Add":
                        action = "ADD_APARTMENT";
                        sendSendStreetRequest();
                        break;
                    case "Show Apartments":
                        try {
                            sendApartmentsToSeller(currentMessage.getFrom().getId());
                        } catch (SQLException e) {
                            reply("Error!");
                            e.printStackTrace();
                        }
                        break;
                    case "Show Wishes":
                        action = "SHOW_WISHES";
                        sendSelectWishesTypeRequest();
                        break;
                    case "Cancel":
                        //unreachable because this situation is handled by the manager
                        break;
                }
                break;
            case "ADD_APARTMENT":
                handleAddAction(currentMessage);
                break;
            case "SHOW_APARTMENTS":
                handleShowApartmentsAction(currentMessage);
                break;
            case "SHOW_WISHES":
                handleShowWishesAction(currentMessage);
                break;
            default:
                throw new IllegalStateException();
        }
    };


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
        runnable = sellerRunnable;

    }

    public SellerSession(Database database, Long chatId) {
        super(database, chatId);
        apartment = new Apartment(); //only for this constructor
        type = "SELLER";
        runnable = sellerRunnable;
    }

    private void sendAllWishesToSeller(Integer sellerId) throws SQLException {
        List<Wish> wishes = database.getAllWishes(sellerId);
        if (wishes.isEmpty()) {
            reply("No good wishes.");
        }
        for (Wish wish : wishes) {
            String callback = "getUser " + wish.buyer;
            InlineKeyboardMarkup getUserKeyboard =
                    Keyboards.makeOneButtonInlineKeyboardMarkup("Get Contact", callback);
            reply(wish.getDescriptionForBuyer(), getUserKeyboard);
        }
    }

    private void sendSmartWishesToSeller(Integer sellerId) throws SQLException {
        List<Wish> wishes = database.getSmartWishesBySellerId(sellerId);
        if (wishes.isEmpty()) {
            reply("No good wishes.");
        }
        for (Wish wish : wishes) {
            String callback = "getUser " + wish.buyer;
            InlineKeyboardMarkup getUserKeyboard =
                    Keyboards.makeOneButtonInlineKeyboardMarkup("Get Contact", callback);
            reply(wish.getDescriptionForBuyer(), getUserKeyboard);
        }
    }

    private void sendApartmentsToSeller(Integer id) throws SQLException {
        LinkedList<Apartment> apartments = database.getApartmentsByTelegramId(id);
        if (apartments.isEmpty()) {
            reply("No added apartments.");
        }
        for (Apartment apartment : apartments) {
            String callback = "deleteApartment " + apartment.databaseId;
            InlineKeyboardMarkup deleteApartmentKeyboard =
                    Keyboards.makeOneButtonInlineKeyboardMarkup("Delete Apartment", callback);
            if (apartment.photos.isEmpty()) {
                reply(apartment.getDescription(), deleteApartmentKeyboard);
            } else {
                replyWithPhoto(apartment.photos.get(0), apartment.getDescription(), deleteApartmentKeyboard);
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
                } else {
                    apartment.street = text;
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
            case "SEND_PICTURE":
                if (message.hasPhoto()) {
                    apartment.addPhoto(message.getPhoto().get(3).getFileId());
                    sendConfirmRequest();
                } else {
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
                            database.addApartment(apartment);
                            reply("Done!");
                            sendSelectActionRequest();
                            apartment.seller=-1;
                            new Notifier(apartment, "APARTMENT").start();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            reply("Error!");
                        }
                        break;
                    case "Back":
                        sendAddPicturesRequest();
                        apartment.photos.clear();
                        break;
                    case "Cancel":
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

    private void handleShowWishesAction(Message currentMessage) {
        try {
            switch (currentMessage.getText()) {
                case "All":
                    sendAllWishesToSeller(currentMessage.getFrom().getId());
                    break;
                case "Smart":
                    sendSmartWishesToSeller(currentMessage.getFrom().getId());
                    break;
            }
        } catch (SQLException e) {
            reply("Error!");
            e.printStackTrace();
        } finally {
            sendSelectActionRequest();
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
        reply("Send me street, please", Keyboards.cancel);
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

    private void sendSelectWishesTypeRequest() {
        LinkedList<String> types = new LinkedList<>();
        types.add("All");
        types.add("Smart");
        reply("Select wishes type", Keyboards.make(types));
        state = "SELECT_WISHES_TYPE";
    }

}
