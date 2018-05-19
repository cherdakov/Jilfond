package com.jilfond.bot;

import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.LinkedList;

public class Keyboards {
    static ReplyKeyboardMarkup onlyCancel = createOnlyCancel();
    static ReplyKeyboardMarkup backAndCancel = createBackAndCancel();


    private static ReplyKeyboardMarkup createOnlyCancel() {
        LinkedList<String> cancel = new LinkedList<>();
        cancel.add("Cancel");
        return make(cancel);
    }
    private static ReplyKeyboardMarkup createBackAndCancel() {
        LinkedList<String> cancel = new LinkedList<>();
        cancel.add("Back");
        cancel.add("Cancel");
        return make(cancel);
    }

    static ReplyKeyboardMarkup make(LinkedList<String> buttons, boolean vertical){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        LinkedList<KeyboardRow> keyboardRows = new LinkedList<>();
        KeyboardRow keyboardButtons = new KeyboardRow();
        for(String button:buttons){
            keyboardButtons.add(button);
        }
        keyboardRows.add(0, keyboardButtons);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return  replyKeyboardMarkup;
    }
    static ReplyKeyboardMarkup make(LinkedList<String> buttons){
        return make(buttons,false);
    }
}
