package com.jilfond.bot;

import java.util.List;

public class Saler extends User {

    List<Apartment> apartmentList;
    public void addAppartment(Apartment apartment){
        apartmentList.add(apartment);
    }
    public List<Apartment> getApartmentList() {
        return apartmentList;
    }
}
