package com.jilfond.bot;

import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.LinkedList;
import java.util.List;

public class Keyboards {
    public static ReplyKeyboardMarkup backCancelAndNo = createBackCancelAndNo();
    public static ReplyKeyboardMarkup cancel = createOneButtonKeyboard("Cancel");
    public static ReplyKeyboardMarkup backAndCancel = createBackAndCancel();
    public static ReplyKeyboardMarkup yesBackAndCancel = createYesBackAndCancel();
    public static ReplyKeyboardMarkup start = createOneButtonKeyboard("/start");

    private static ReplyKeyboardMarkup createOneButtonKeyboard(String text) {
        LinkedList<String> buttons = new LinkedList<>();
        buttons.add(text);
        return make(buttons);
    }

    public static InlineKeyboardMarkup makeOneButtonInlineKeyboardMarkup(String buttonText, String callbackData){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton().setCallbackData(callbackData).setText(buttonText);
        List<List<InlineKeyboardButton>> linkedListLinkedList = new LinkedList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonLinkedList= new LinkedList<>();
        inlineKeyboardButtonLinkedList.add(inlineKeyboardButton);
        linkedListLinkedList.add(inlineKeyboardButtonLinkedList);
        inlineKeyboardMarkup.setKeyboard(linkedListLinkedList);
        return inlineKeyboardMarkup;
    }

    private static ReplyKeyboardMarkup createBackCancelAndNo() {
        LinkedList<String> buttons = new LinkedList<>();
        buttons.add("Back");
        buttons.add("Cancel");
        buttons.add("No");
        return make(buttons);
    }

    private static ReplyKeyboardMarkup createBackAndCancel() {
        LinkedList<String> buttons = new LinkedList<>();
        buttons.add("Back");
        buttons.add("Cancel");
        return make(buttons);
    }

    private static ReplyKeyboardMarkup createYesBackAndCancel() {
        LinkedList<String> buttons = new LinkedList<>();
        buttons.add("Yes");
        buttons.add("Back");
        buttons.add("Cancel");
        return make(buttons);
    }

    public static ReplyKeyboardMarkup make(LinkedList<String> buttons, boolean vertical){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        LinkedList<KeyboardRow> keyboardRows = new LinkedList<>();
        if(vertical) {
            for(String button: buttons){
                KeyboardRow keyboardButtons = new KeyboardRow();
                keyboardButtons.add(button);
                keyboardRows.add(keyboardButtons);
            }
        } else{
            KeyboardRow keyboardButtons = new KeyboardRow();
            for (String button : buttons) {
                keyboardButtons.add(button);
            }
            keyboardRows.add(keyboardButtons);
        }
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return  replyKeyboardMarkup;
    }
    public static ReplyKeyboardMarkup make(LinkedList<String> buttons){
        return make(buttons,false);
    }
}
