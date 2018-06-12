package com.jilfond.bot.sessions;

import com.jilfond.bot.Keyboards;
import com.jilfond.bot.databases.Database;
import com.jilfond.bot.managers.Notifier;
import com.jilfond.bot.objects.Apartment;
import com.jilfond.bot.objects.Wish;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class BuyerSession extends Session {
    private Wish wish;
    private Runnable buyerRunnable = () -> {
        switch (action) {
            case "NONE":
                switch (currentMessage.getText()) {
                    case "Add":
                        action = "ADD_WISH";
                        sendSendStreetRequest();
                        break;
                    case "Show Wishes":
                        try {
                            sendWishesToBuyer(currentMessage.getFrom().getId());
                        } catch (SQLException e) {
                            reply("Error!");
                            e.printStackTrace();
                        }
                        break;
                    case "Show Apartments":
                        action = "SHOW_APARTMENTS";
                        sendSelectApartmentsTypeRequest();
                        break;
                    case "Cancel":
                        //unreachable because this situation is handled by the manager
                        break;
                }
                break;
            case "ADD_WISH":
                handleAddAction(currentMessage);
                break;
            case "SHOW_WISHES":
                handleShowWishesAction(currentMessage);
                break;
            case "SHOW_APARTMENTS":
                handleShowApartmentsAction(currentMessage);
                break;
            default:
                throw new IllegalStateException();
        }
    };

    private void sendSelectApartmentsTypeRequest() {
        LinkedList<String> types = new LinkedList<>();
        types.add("All");
        types.add("Smart");
        reply("Select Apartments type", Keyboards.make(types));
        state = "SELECT_APARTMENTS_TYPE";
    }

    private void handleShowApartmentsAction(Message currentMessage) {
        try {
            switch (currentMessage.getText()) {
                case "All":
                    sendAllApartmentsToBuyer(currentMessage.getFrom().getId());
                    break;
                case "Smart":
                    sendSmartApartmentsToBuyer(currentMessage.getFrom().getId());
                    break;
            }
        } catch (SQLException e) {
            reply("Error!");
            e.printStackTrace();
        } finally {
            sendSelectActionRequest();
        }
    }

    private void sendSmartApartmentsToBuyer(Integer buyerId) throws SQLException {
        List<Apartment> apartments = database.getSmartApartmentsByBuyerId(buyerId);
        if (apartments.isEmpty()) {
            reply("No good apartments.");
        }
        for (Apartment apartment : apartments) {
            sendApartmentToBuyer(apartment,buyerId);
        }
    }


    private void sendAllApartmentsToBuyer(Integer buyerId) throws SQLException {
        List<Apartment> apartments = database.getAllApartmentsByBuyerId(buyerId);
        if (apartments.isEmpty()) {
            reply("No good apartments.");
        }
        for (Apartment apartment : apartments) {
            sendApartmentToBuyer(apartment,buyerId);
        }
    }
    private void sendApartmentToBuyer(Apartment apartment,Integer buyerId){
        LinkedList<InlineKeyboardButton> buttons = new LinkedList<>();
        buttons.add(Keyboards.makeInlineButton("Get Contact", "getUser " + apartment.seller));
        buttons.add(Keyboards.makeInlineButton("Not interest", "ignoreApartment " + buyerId + " " + apartment.databaseId));
        InlineKeyboardMarkup inlineKeyboardMarkup = Keyboards.makeInlineKeyboardMarkup(buttons);
        if (apartment.photos.isEmpty()) {
            reply(apartment.getDescription(), inlineKeyboardMarkup);
        } else {
            replyWithPhoto(apartment.photos.get(0), apartment.getDescription(), inlineKeyboardMarkup);
        }
    }

    @Override
    protected Object getObject() {
        return wish;
    }

    @Override
    protected void setObject(Object object) {
        wish = (Wish) object;
    }

    public BuyerSession(Database database, Long chatId) {
        super(database, chatId);
        type = "BUYER";
        wish = new Wish();//only for this constructor
        runnable = buyerRunnable;
    }

    public BuyerSession(Database database, SessionDescription sessionDescription) {
        super(database, sessionDescription);
        runnable = buyerRunnable;
    }



    private void handleShowWishesAction(Message message) {
    }

    private void handleAddAction(Message message) {
        String text = message.getText();
        switch (state) {
            case "SEND_STREET":
                if (text.equals("Cancel")) {
                    sendSelectActionRequest();
                } else {
                    wish.street = text;
                    sendSendPriceRequest();
                }
                break;
            case "SEND_PRICE":
                switch (text) {
                    case "Cancel":
                        sendSelectActionRequest();
                        break;
                    case "Back":
                        sendSendStreetRequest();
                        break;
                    default:
                        try {
                            wish.price = Integer.parseInt(text);
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
                            wish.square = Integer.parseInt(text);
                            wish.buyer = message.getFrom().getId();
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
                            database.addWish(wish);
                            reply("Done!");
                            sendSelectActionRequest();
                            new Notifier(wish, "WISH").start();
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
                System.out.println(state);
                System.out.println(message.toString());
                throw new IllegalStateException();
        }
    }

    private void sendWishesToBuyer(Integer id) throws SQLException {
        LinkedList<Wish> wishes = database.getWishesByTelegramId(id);
        if (wishes.isEmpty()) {
            reply("No added wishes.");
        }
        for (Wish wish : wishes) {
            String callback = "deleteWish " + wish.databaseId;
            InlineKeyboardMarkup deleteWishKeyboard =
                    Keyboards.makeOneButtonInlineKeyboardMarkup("Delete Wish", callback);
            reply(wish.getDescriptionForBuyer(), deleteWishKeyboard);
        }
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


    private void sendConfirmRequest() {
        reply("Confirm information", Keyboards.yesBackAndCancel);
        reply(wish.toString());
        state = "CONFIRM";
    }
}
