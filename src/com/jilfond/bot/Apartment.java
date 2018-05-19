package com.jilfond.bot;

public class Apartment {
    public String street;
    public Integer number;
    public Integer price;
    public Seller seller;

    public Apartment(String street, Integer number, Integer price, Seller seller) {
        this.street = street;
        this.number = number;
        this.price = price;
        this.seller = seller;
    }
}
