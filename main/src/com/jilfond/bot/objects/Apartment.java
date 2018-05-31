package com.jilfond.bot.objects;

import org.telegram.telegrambots.api.objects.PhotoSize;

import java.util.LinkedList;
import java.util.List;

public class Apartment {
    public String street = "street";
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

    public Apartment() {
        photos = new LinkedList<>();
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
                ", seller=" + seller +
                ", photos=" + photos +
                '}';
    }

    public void setPhotos(List<PhotoSize> photos) {
        this.photos = new LinkedList<>();
        for (PhotoSize photo : photos) {
            this.photos.add(photo.getFileId());
        }
    }
}
