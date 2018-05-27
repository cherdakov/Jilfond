package com.jilfond.bot.objects;

import org.telegram.telegrambots.api.objects.PhotoSize;

import java.util.LinkedList;
import java.util.List;

public class Apartment {
    private String street = "street";
    public String houseNumber = "houseNumber";
    public Integer number = 0;
    public Integer databaseId = 0;
    public Integer price = 0;
    public Integer square = 0;
    public Integer seller = 0;
    public List<String> photos;


    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street.replace('\"', '\'');
    }

    public Apartment() {
        photos = new LinkedList<>();
        photos.add("photo");
    }

    @Override
    public String toString() {
        return "street = " + street + "\n" +
                "houseNumber = " + houseNumber + "\n" +
                "number = " + number + "\n" +
                "price = " + price + "\n" +
                "square = " + square + "\n" +
                "seller = " + seller + "\n";
    }

    public void setPhotos(List<PhotoSize> photos) {
        this.photos = new LinkedList<>();
        for (PhotoSize photo : photos) {
            this.photos.add(photo.getFileId());
        }
    }
}
