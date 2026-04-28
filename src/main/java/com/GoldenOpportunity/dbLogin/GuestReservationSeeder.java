package com.GoldenOpportunity.dbLogin;

/**
 * Initializes the guest/reservation SQLite schema and synchronizes guest users.
 */
public class GuestReservationSeeder {
    public static void main(String[] args) throws Exception {
        GuestReservationDao dao = new GuestReservationDao();
        dao.initializeSchema();

        int guestCount = dao.syncGuestUsersFromUsersTable();
        dao.refreshGuestReservationIds();
        System.out.printf("SQLite guest/reservation schema ready. Guests synced: %d.%n", guestCount);
    }
}
