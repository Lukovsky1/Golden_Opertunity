package com.GoldenOpportunity.Roles;

import com.GoldenOpportunity.DatabaseTools.DBUtil;
import com.GoldenOpportunity.ReservationService;
import com.GoldenOpportunity.Shop.Order;
import com.GoldenOpportunity.User;
import com.GoldenOpportunity.Login.enums.Role;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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

    //TODO: Implement
    /*public void checkIn(String resId) throws SQLException {
        String check = "UPDATE Reservations SET checkIn = 1 WHERE resId = " + resId + ";";
        try {
        Connection conn = DBUtil.getConnection();
        Statement stmt = conn.createStatement();

        conn.setAutoCommit(false);
        stmt.execute(check);
        conn.commit();
        } catch (SQLException e) {


        }
    }*/
}
