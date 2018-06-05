package com.jilfond.bot.objects;

public class Wish {
    public String street = "street";
    public int price = 0;
    public int square = 0;
    public Integer buyer = 0;
    public int databaseId = 0;

    @Override
    public String toString() {
        return "Wish{" +
                "street='" + street + '\'' +
                ", price=" + price +
                ", square=" + square +
                '}';
    }

    public String getDescriptionForBuyer() {
        return "street = " + street + "\n" +
               "price = " + price + "\n" +
               "square = " + square;
    }
}
