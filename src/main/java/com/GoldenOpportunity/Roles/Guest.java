package com.GoldenOpportunity.Roles;

import com.GoldenOpportunity.User;
import com.GoldenOpportunity.Login.enums.Role;

/*
public interface Guest extends User{
    boolean corporate = false;
    int guestID = -1;

    Reservation reservation = new Reservation();
    ShoppingCart shoppingCart = new ShoppingCart();

    void requestReservationModification(int resId, String details);
}
*/

public class Guest extends User {
    private final boolean corporate;

    public Guest(int id, String username, String password, String contactInfo, boolean corporate) {
        super(id, username, password, contactInfo, Role.GUEST);
        this.corporate = corporate;
    }

    public boolean isCorporate() {
        return corporate;
    }
}