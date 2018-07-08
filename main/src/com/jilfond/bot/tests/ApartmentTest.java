package com.jilfond.bot.tests;

import com.jilfond.bot.objects.Apartment;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ApartmentTest {

    @Test
    @Disabled
    void toStringTest() {
        Apartment apartment = new Apartment();
        apartment.street = "Caesars";
        apartment.houseNumber = "123/2";
        apartment.number = 23;
        apartment.seller = 123454321;
        apartment.square = 31;
        apartment.price = 12345;
        System.out.println(apartment.toString());
    }
}