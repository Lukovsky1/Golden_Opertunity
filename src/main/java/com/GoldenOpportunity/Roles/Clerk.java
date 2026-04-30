package com.GoldenOpportunity.Roles;

import com.GoldenOpportunity.*;
import com.GoldenOpportunity.DatabaseTools.DBUtil;
import com.GoldenOpportunity.Shop.Order;
import com.GoldenOpportunity.Login.enums.Role;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

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


    public boolean modifyInfo(int id, String username, String password, String contactInfo) throws SQLException {
        String sql = "UPDATE users SET username=?, password=?, contact_info=?, updated_at=? WHERE id=?";

        Clerk clerk = findClerk(id);
        if (clerk == null) return false;

        if (username.isBlank()) username = clerk.getUsername();
        if (password.isBlank()) password = clerk.getPassword();
        if (contactInfo.isBlank()) contactInfo = clerk.getContactInfo();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, contactInfo);
            stmt.setString(4, LocalDate.now().toString());
            stmt.setInt(5, id);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw e;
        }
        return true;
    }


    // used for valid store purchase use case, minimal for now
    public void notifyOrder(Order order) {
        System.out.println("Clerk notified for order #" + order.getOrderID());
    }

    public void modifyRoom(Criteria criteria){
        SearchController searchController = new SearchController(new RoomService(),
                new ReservationService());
        searchController.getRoomService().modifyRoom(criteria);
    }

    public Receipt cancelReservation(String resID) throws SQLException {
        SearchController sc = new SearchController(new RoomService(), new ReservationService());
        Reservation reservation = sc.getResService().findReservation(resID);

        if (reservation == null) return null;

        LocalDate today = LocalDate.now();
        LocalDate start = reservation.getDateRange().startDate();
        LocalDate cutoff = start.minusDays(2);

        Receipt receipt = reservation.getReceipt();

        if (!today.isBefore(cutoff)) {
            double penalty = reservation.getRooms()
                    .stream()
                    .mapToDouble(Room::getRate)
                    .sum() * 0.8;

            receipt.setPenalty(penalty);
            receipt.addOnTotal(penalty);
        }

        sc.getResService().deleteReservation(resID);
        return receipt;
    }

    public Receipt generateBilling(String resID) throws SQLException {
        SearchController sc = new SearchController(new RoomService(), new ReservationService());
        Reservation reservation = sc.getResService().findReservation(resID);

        if (reservation == null) return null;

        Receipt receipt = reservation.getReceipt();
        LocalDate today = LocalDate.now();
        LocalDate end = reservation.getDateRange().endDate();

        if (today.isBefore(end)) {
            double penalty = reservation.getRooms()
                    .stream()
                    .mapToDouble(Room::getRate)
                    .sum() * 0.8;

            receipt.setPenalty(penalty);
        }

        receipt.calculateTotal();
        return receipt;
    }

    public boolean checkout(String resID) throws SQLException {
        SearchController searchController = new SearchController(new RoomService(), new ReservationService());
        if(searchController.getResService().findReservation(resID).isCheckedIn()){
            searchController.getResService().deleteReservation(resID);
            return true;
        }
        return false;
    }

    public boolean checkIn(String resID) throws SQLException{
        String sql = "UPDATE Reservations SET checkedIn= ? WHERE resId = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            stmt.setBoolean(1, true);
            stmt.setString(2, resID);

            stmt.executeUpdate();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            DBUtil.getConnection().rollback();
            throw e;
        }
        return true;
    }

     static public Clerk findClerk(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ? AND role = 'CLERK'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return buildClerkFromResultSet(rs);
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    static private Clerk buildClerkFromResultSet(ResultSet rs) throws SQLException{
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String password = rs.getString("password_hash");
        String contactInfo = rs.getString("contact_info");

        return new Clerk(id, username, password, contactInfo);
    }
}
