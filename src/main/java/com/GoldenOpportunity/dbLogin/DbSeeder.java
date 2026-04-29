package com.GoldenOpportunity.dbLogin;

import com.GoldenOpportunity.DatabaseTools.DBUtil;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Small helper with a {@code main} method to create the DB and ensure demo users exist.
 *
 * This is intended for local development and manual testing of the login UI.
 */
//TODO: Need to separate the seeding logic from the loadingInsertStatements
public class DbSeeder {
    public static void main(String[] args) throws SQLException, IOException {
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
        guestReservationDao.assignReservationToGuest("guest2", "R-201");
        guestReservationDao.assignReservationToGuest("guest2", "R-202");

        /*try {
            LoadingInserts.createRooms(DBUtil.getConnection());
            LoadingInserts.createReservations(DBUtil.getConnection());
            LoadingInserts.createReservedRooms(DBUtil.getConnection());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return;
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } */


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
