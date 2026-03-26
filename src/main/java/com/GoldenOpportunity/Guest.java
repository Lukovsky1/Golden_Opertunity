package com.GoldenOpportunity;

import com.GoldenOpportunity.Shop.ShoppingCart;

public interface Guest extends User{
    boolean corporate = false;
    int guestID = -1;

    //Reservation reservation = new Reservation();
    ShoppingCart shoppingCart = new ShoppingCart();

    void requestReservationModification(int resId, String details);
}
