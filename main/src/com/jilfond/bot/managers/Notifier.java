package com.jilfond.bot.managers;

import com.jilfond.bot.Bot;
import com.jilfond.bot.Keyboards;
import com.jilfond.bot.databases.Database;
import com.jilfond.bot.objects.Apartment;
import com.jilfond.bot.objects.BotUser;
import com.jilfond.bot.objects.Wish;

import java.sql.SQLException;
import java.util.LinkedList;

public class Notifier extends Thread {
    public Notifier(Object object, String type) {
        super(() -> {
            try {
                Bot bot = Bot.getCurrentBot();
                Database database = new Database();
                LinkedList<BotUser> users;
                System.out.println("Notifier");
                switch (type) {
                    case "WISH":
                        Wish wish = (Wish) object;
                        users = database.getUsersWithApartmentsOnStreet(wish.street, wish.buyer);
                        for(BotUser user:users){
                            System.out.println(user.toString());
                            bot.send((long) user.telegramId,wish.toString());
                        }
                        break;
                    case "APARTMENT":
                        Apartment apartment = (Apartment) object;
                        users = database.getUsersWithWishesOnStreet(apartment.street, apartment.seller);
                        break;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        setPriority(1);
    }
}
