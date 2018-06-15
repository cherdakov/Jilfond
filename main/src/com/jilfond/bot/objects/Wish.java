package com.jilfond.bot.objects;

public class Wish {
    public String street = "street";
    public Integer price = 0;
    public Integer square = 0;
    public Integer buyer = 0;
    public Integer databaseId = 0;
    public Integer rooms = 0;

    @Override
    public String toString() {
        return "Wish{" +
                "street='" + street + '\'' +
                ", price=" + price +
                ", square=" + square +
                ", buyer=" + buyer +
                ", databaseId=" + databaseId +
                ", rooms=" + rooms +
                '}';
    }

    public String getDescription() {
        return "street = " + street + "\n" +
               "rooms = " + rooms + "\n" +
               "price = " + price + "\n" +
               "square = " + square;
    }
}
