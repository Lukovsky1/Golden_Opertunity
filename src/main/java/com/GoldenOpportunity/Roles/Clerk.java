package com.GoldenOpportunity.Roles;

import com.GoldenOpportunity.*;
import com.GoldenOpportunity.Shop.Order;
import com.GoldenOpportunity.Login.enums.Role;

import java.nio.file.Path;

/**
 * Concrete user type representing a front-desk clerk.
 * Uses the {@link Role#CLERK} role to grant operational privileges.
 */
public class Clerk extends User {
    /**
     * Constructs a clerk user with the CLERK role.
     *
     * @param id unique user identifier
     * @param username login name
     * @param password plaintext password (to be replaced by hashing in future)
     * @param contactInfo contact details for the user
     */
    public Clerk(int id, String username, String password, String contactInfo) {
        super(id, username, password, contactInfo, Role.CLERK);
    }

    // used for valid store purchase use case, minimal for now
    public void notifyOrder(Order order) {
        System.out.println("Clerk notified for order #" + order.getOrderID());
    }

    public void cancelReservation(String resID){
        SearchController searchController = new SearchController(new RoomService(),
                new ReservationService(Path.of("src/main/resources/testReservationData1.csv")));
        searchController.getResService().deleteReservation(resID);
    }

    public void modifyRoom(Criteria criteria){
        SearchController searchController = new SearchController(new RoomService(),
                new ReservationService(Path.of("src/main/resources/testReservationData1.csv")));
        searchController.getRoomService().modifyRoom(criteria);
    }
}
