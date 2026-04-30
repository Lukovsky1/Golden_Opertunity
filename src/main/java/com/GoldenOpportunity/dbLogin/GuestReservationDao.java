package com.GoldenOpportunity.dbLogin;

import com.GoldenOpportunity.DatabaseTools.DBInitializer;
import com.GoldenOpportunity.DatabaseTools.DBUtil;
import org.sqlite.core.DB;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Synchronizes guest users and guest-to-reservation assignments into SQLite.
 */
public class GuestReservationDao {
    public void initializeSchema() throws SQLException, IOException {
        DBInitializer.initialize();
    }

    public int syncGuestUsersFromUsersTable() throws SQLException {
        String sql = """
            INSERT INTO guests (guest_id, name, email, resId, created_at, updated_at)
            SELECT id, username, contact_info, NULL, created_at, updated_at
            FROM users
            WHERE role = 'GUEST'
            ON CONFLICT(guest_id) DO UPDATE SET
                name = excluded.name,
                email = excluded.email,
                updated_at = excluded.updated_at
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            return ps.executeUpdate();
        }
    }

    public void seedReservationForGuest(String username,
                                        String reservationId,
                                        int roomNumber,
                                        String startDate,
                                        String endDate,
                                        double bill) throws SQLException {
        assignReservationToGuest(username, reservationId);
    }

    public void assignReservationToGuest(String username, String reservationId) throws SQLException {
        if (findGuestIdByUsername(username) == null) {
            throw new IllegalStateException("Guest user not found for reservation seed: " + username);
        }

        String sql = """
            UPDATE guests
            SET resId = CASE
                    WHEN resId IS NULL OR TRIM(resId) = '' THEN ?
                    WHEN instr(',' || replace(resId, ' ', '') || ',', ',' || replace(?, ' ', '') || ',') > 0 THEN resId
                    ELSE resId || ', ' || ?
                END
            WHERE guest_id = (
                SELECT g.guest_id
                FROM guests g
                JOIN users u ON u.id = g.guest_id
                WHERE u.username = ?
            )
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reservationId);
            ps.setString(2, reservationId);
            ps.setString(3, reservationId);
            ps.setString(4, username);
            ps.executeUpdate();
        }
    }

    public void refreshGuestReservationIds() throws SQLException {
        // Reservation ownership is stored directly on the guest row as resId.
    }

    private Integer findGuestIdByUsername(String username) throws SQLException {
        String sql = """
            SELECT g.guest_id
            FROM guests g
            JOIN users u ON u.id = g.guest_id
            WHERE u.username = ?
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return null;
            }
        }
    }

    public List<String> findGuestResIDs(int id){
        List<String> reservations = new ArrayList<>();
        String sql = "SELECT resID FROM guests WHERE guest_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                while(rs.next()){
                    reservations.add(rs.getString("resID"));
                }
                return reservations;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
