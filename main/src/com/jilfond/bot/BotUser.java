package com.jilfond.bot;

import org.telegram.telegrambots.api.objects.User;

import java.lang.reflect.Field;

public class BotUser {
    public int telegramId;
    public String firstName = "null";
    public String lastName = "null";
    public String userName = "null";
    public String phoneNumber = "null";
    public String email = "null";

    @Override
    public String toString() {
        return "BotUser{" +
                "telegramId=" + telegramId +
                ", firstName=" + firstName +
                ", lastName=" + lastName +
                ", userName=" + userName +
                ", phoneNumber=" + phoneNumber +
                ", email=" + email +
                '}';
    }

    public BotUser(){

    }

    public BotUser(User user) {
        if (user.getId() != null) {
            telegramId = user.getId();
        }
        if (user.getFirstName() != null) {
            firstName = user.getFirstName();
        }
        if (user.getLastName() != null) {
            lastName = user.getLastName();
        }
        if (user.getUserName() != null) {
            userName = user.getUserName();
        }
    }


    public String getValuesForDB() {
        //return String format (telegramId, firstName, lastName, userName, phoneNumber, email)
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        stringBuilder.append(telegramId);
        stringBuilder.append(", ");
        if (firstName.equals("null")) {
            stringBuilder.append(firstName);
        } else {
            stringBuilder.append("'");
            stringBuilder.append(firstName);
            stringBuilder.append("'");
        }
        stringBuilder.append(", ");
        if (lastName.equals("null")) {
            stringBuilder.append(lastName);
        } else {
            stringBuilder.append("'");
            stringBuilder.append(lastName);
            stringBuilder.append("'");
        }
        stringBuilder.append(", ");
        if (userName.equals("null")) {
            stringBuilder.append(userName);
        } else {
            stringBuilder.append("'");
            stringBuilder.append(userName);
            stringBuilder.append("'");
        }
        stringBuilder.append(", ");
        if (phoneNumber.equals("null")) {
            stringBuilder.append(phoneNumber);
        } else {
            stringBuilder.append("'");
            stringBuilder.append(phoneNumber);
            stringBuilder.append("'");
        }
        stringBuilder.append(", ");
        if (email.equals("null")) {
            stringBuilder.append(email);
        } else {
            stringBuilder.append("'");
            stringBuilder.append(email);
            stringBuilder.append("'");
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}