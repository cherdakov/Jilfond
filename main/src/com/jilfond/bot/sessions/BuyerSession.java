package com.jilfond.bot.sessions;

import com.jilfond.bot.Keyboards;
import com.jilfond.bot.databases.Database;
import com.jilfond.bot.objects.Wish;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.sql.SQLException;
import java.util.LinkedList;

public class BuyerSession extends Session {
    private Wish wish;
    private Runnable buyerRunnable = new Runnable() {
        @Override
        public void run() {
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
                    //handleShowWishAction(currentMessage);
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
    };

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
        wish = new Wish();

    }

    public BuyerSession(Database database, SessionDescription sessionDescription) {
        super(database, sessionDescription);
    }



    private void handleShowWishesAction(Message message) {
    }

    private void handleAddAction(Message message) {
        String text = message.getText();
        switch (state) {
            case "SEND_STREET":
                if (text.equals("Cancel")) {
                    sendSelectActionRequest();
                    action = "NONE";
                } else {
                    wish.street = text;
                    sendSendPriceRequest();
                }
                break;
            case "SEND_PRICE":
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
                        action = "NONE";
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
                        } catch (SQLException e) {
                            e.printStackTrace();
                            reply("Error!");
                        }
                        action = "NONE";
                        break;
                    case "Back":
                        sendSendSquareRequest();
                        break;
                    case "Cancel":
                        action = "NONE";
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
        reply(wish.toString());
        state = "CONFIRM";
    }
}
