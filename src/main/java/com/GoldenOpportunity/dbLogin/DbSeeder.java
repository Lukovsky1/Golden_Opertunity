package com.GoldenOpportunity.dbLogin;

import java.sql.SQLException;

/**
 * Small helper with a {@code main} method to create the DB and ensure demo users exist.
 *
 * This is intended for local development and manual testing of the login UI.
 */
public class DbSeeder {
    public static void main(String[] args) throws SQLException {
        UserDao dao = new UserDao();
        dao.initializeSchema(); // Create DB + tables on first run

        // Seed only if missing so you can safely run this multiple times.
        seedIfMissing(dao, "admin1", "adminpass", "ADMIN", "admin@golden.com");
        seedIfMissing(dao, "clerk1", "clerkpass", "CLERK", "clerk@golden.com");
        seedIfMissing(dao, "guest1", "guestpass", "GUEST", "guest@golden.com");
        seedIfMissing(dao, "guest2", "guestpass2", "GUEST", "guest2@golden.com");

        GuestReservationDao guestReservationDao = new GuestReservationDao();
        guestReservationDao.initializeSchema();
        guestReservationDao.syncGuestUsersFromUsersTable();
        guestReservationDao.seedReservationForGuest("guest2", "R-201", 101, "2026-05-15", "2026-05-18", 450.00);
        guestReservationDao.seedReservationForGuest("guest2", "R-202", 204, "2026-06-02", "2026-06-05", 525.00);

        System.out.println("SQLite database initialized and sample users ensured.");
    }

    private static void seedIfMissing(UserDao dao, String username, String password, String role, String contact) {
        try {
            if (dao.findByUsername(username) == null) {
                dao.createUser(username, password, role, contact);
                System.out.printf("Created user '%s' with role %s%n", username, role);
            } else {
                System.out.printf("User '%s' already exists%n", username);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed seeding user: " + username, e);
        }
    }
}
