package com.jilfond.bot.objects;


import java.util.LinkedList;
import java.util.List;

public class Apartment {
    public String street = "street";
    public String houseNumber = "houseNumber";
    public Integer number = 0;
    public Integer databaseId = 0;
    public Integer price = 0;
    public Integer square = 0;
    public Integer floor = 0;
    public Integer rooms = 0;
    public Integer seller = 0;
    public List<String> photos = new LinkedList<>();

    public Apartment() {

    }

    @Override
    public String toString() {
        return "Apartment{" +
                "street='" + street + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", number=" + number +
                ", databaseId=" + databaseId +
                ", price=" + price +
                ", square=" + square +
                ", floor=" + floor +
                ", rooms=" + rooms +
                ", seller=" + seller +
                ", photos=" + photos +
                '}';
    }

    public String getDescription() {
        return "street = " + street + "\n" +
                "houseNumber = " + houseNumber + "\n" +
                "number = " + number + "\n" +
                "price = " + price + "\n" +
                "floor = " + floor + "\n" +
                "rooms = " + rooms + "\n" +
                "square = " + square;
    }


    public void addPhoto(String fileId) {
        photos.add(fileId);
    }
}
