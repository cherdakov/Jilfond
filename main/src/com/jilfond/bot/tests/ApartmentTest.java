package com.jilfond.bot.tests;

import com.jilfond.bot.objects.Apartment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApartmentTest {

    @Test
    void toStringTest() {
        Apartment apartment = new Apartment();
        apartment.setStreet("Caesars");
        apartment.houseNumber = "123/2";
        apartment.apartmentNumber = 23;
        apartment.seller = 123454321;
        apartment.square = 31;
        apartment.price = 12345;
        System.out.println(apartment.toString());
    }
}