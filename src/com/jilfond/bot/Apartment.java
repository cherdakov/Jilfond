package com.jilfond.bot;

import java.util.LinkedList;
import java.util.List;

public class Apartment {
    public String street;
    public Integer number;
    public Integer price;
    List<String> photos = new LinkedList<>();

    public Apartment() {
    }

    public Apartment(String street, Integer number, Integer price, List<String> photos) {
        this.street = street;
        this.number = number;
        this.price = price;
        this.photos = photos;
    }

    @Override
    public String toString() {
        return  "street = " + street + "\n" +
                "number = " + number + "\n" +
                "price = "  + price  + "\n";
    }
}
