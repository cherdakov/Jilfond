package com.jilfond.bot;

import java.util.LinkedList;
import java.util.List;

public class Seller extends User {

    LinkedList<Apartment> apartmentList = new LinkedList<>();

    public void addAppartment(Apartment apartment){
        apartmentList.add(apartment);
    }
    public List<Apartment> getApartmentList() {
        return apartmentList;
    }
}
