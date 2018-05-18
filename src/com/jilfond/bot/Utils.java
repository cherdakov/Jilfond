package com.jilfond.bot;

import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.LinkedList;

public class Utils {

    static ReplyKeyboardMarkup makeKeyboard(LinkedList<String> buttons, boolean vertical){
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
    static ReplyKeyboardMarkup makeKeyboard(LinkedList<String> buttons){
        return makeKeyboard(buttons,false);
    }
}
