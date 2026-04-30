package com.GoldenOpportunity.Roles;

import com.GoldenOpportunity.*;
import com.GoldenOpportunity.Login.enums.Role;
import com.GoldenOpportunity.dbLogin.DbUser;
import com.GoldenOpportunity.dbLogin.GuestReservationDao;
import com.GoldenOpportunity.dbLogin.UserDao;

import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
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

    //Can return a null value
    static public Guest getGuestFromID(int id) throws SQLException {
        UserDao userDao = new UserDao();
        DbUser dbUser = userDao.findById(id);
        GuestReservationDao guestReservationDao = new GuestReservationDao();
        ReservationService resService = new ReservationService();

        List<String> reservationIDs = guestReservationDao.findGuestReservations(id);
        List<Reservation> reservations = new ArrayList<>();
        /* if (reservationIDs == null ||  reservationIDs.isEmpty() ) {
            return null;
        } */
        for (String reserveIDs : reservationIDs) {
            reservations.add(resService.findReservation(reserveIDs));
        }

        //TODO: Need to add corporate check inside of the guest database
        return new Guest(id, dbUser.username, dbUser.passwordHash, dbUser.contactInfo, false,
                reservations);
    }

    static public List<Guest> getAllGuests() throws SQLException {
        UserDao userDao = new UserDao();
        List<DbUser> allGuestRoles = userDao.findByRole("GUEST");
        List<Guest> guests = new ArrayList<>();
        for (DbUser dbUser : allGuestRoles) {
            Guest newGuest = getGuestFromID(dbUser.id);
            guests.add(newGuest);
        }
        return guests;
    }

    @Override
    public String toString() {
        return getUsername() + " (" +  getUserId() + "): " + "Email" + getContactInfo() + ", Reservations: " + reservations;
    }

}
