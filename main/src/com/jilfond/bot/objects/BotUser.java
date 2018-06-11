package com.jilfond.bot.objects;

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
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public String getContact() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Contact details:\n");
        if (firstName != null && !firstName.equals("null")) {
            stringBuilder.append(firstName + "\n");
        }
        if (lastName != null && !lastName.equals("null")) {
            stringBuilder.append(lastName + "\n");
        }
        if (userName != null && !userName.equals("null")) {
            stringBuilder.append("@" + userName + "\n");
        }
        if (phoneNumber != null && !phoneNumber.equals("null")) {
            stringBuilder.append(phoneNumber + "\n");
        }
        if (email != null && !email.equals("null")) {
            stringBuilder.append(email + "\n");
        }
        return stringBuilder.toString();
    }

    public BotUser() {

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

}