package com.GoldenOpportunity.Roles;

import com.GoldenOpportunity.*;
import com.GoldenOpportunity.Login.enums.Role;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/*
 Legacy concept for a Guest interface retained for reference. The system now
 models Guest as a concrete class extending User with a 'corporate' flag.
*/

/**
 * Concrete user type representing a guest customer.
 */
public class Guest extends User {
    /** Indicates whether the guest is associated with a corporate account. */
    private final boolean corporate;
    List<Reservation> reservations;





    /**
     * Constructs a guest user with the GUEST role.
     *
     * @param id unique user identifier
     * @param username login name
     * @param password plaintext password (to be replaced by hashing in future)
     * @param contactInfo contact details for the user
     * @param corporate whether this guest is a corporate customer
     */
    public Guest(int id, String username, String password, String contactInfo, boolean corporate,
                 List<Reservation> reservations) {
        super(id, username, password, contactInfo, Role.GUEST);
        this.corporate = corporate;
    }

    /**
     * @return true if the guest is a corporate customer; false otherwise
     */
    public boolean isCorporate() {
        return corporate;
    }
    public List<Reservation> getReservations() {
        return reservations;
    }

    //TODO: Implement
    public void reserveRoom(LocalDate startDate, LocalDate endDate, Room room) {
        SearchController searchController = new SearchController(new RoomService(), new ReservationService());
    }

}
