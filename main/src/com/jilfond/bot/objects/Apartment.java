package com.jilfond.bot.objects;

import java.util.LinkedList;
import java.util.List;

public class Apartment {
    private String street;
    public String houseNumber;
    public Integer apartmentNumber;
    public Integer price;
    public Integer square;
    public Integer seller;
    List<String> photos = new LinkedList<>();


    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street.replace('\"', '\'');
    }

    public Apartment() {
    }

    @Override
    public String toString() {
        return  "street = " + street + "\n" +
                "houseNumber = " + houseNumber + "\n" +
                "apartmentNumber = " + apartmentNumber + "\n" +
                "price = " + price + "\n" +
                "square = " + square + "\n" +
                "seller = " + seller + "\n";
    }
}
