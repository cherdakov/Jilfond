package com.jilfond.bot;

import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.LinkedList;

public class Utils {

    static ReplyKeyboardMarkup makeKeyboard(LinkedList<String> buttons, boolean vertical){

    }
    static ReplyKeyboardMarkup makeKeyboard(LinkedList<String> buttons){
        return makeKeyboard(buttons,false);
    }
}
