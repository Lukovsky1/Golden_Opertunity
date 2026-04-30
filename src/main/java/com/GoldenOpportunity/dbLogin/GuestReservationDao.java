package com.GoldenOpportunity.dbLogin;

import com.GoldenOpportunity.DatabaseTools.DBInitializer;
import com.GoldenOpportunity.DatabaseTools.DBUtil;
import org.sqlite.core.DB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public void replaceReservationsForGuest(String username, java.util.List<String> reservationIds) throws SQLException {
        if (findGuestIdByUsername(username) == null) {
            throw new IllegalStateException("Guest user not found for reservation seed: " + username);
        }

        String joinedReservationIds = String.join(", ", reservationIds);
        String sql = """
            UPDATE guests
            SET resId = ?
            WHERE guest_id = (
                SELECT g.guest_id
                FROM guests g
                JOIN users u ON u.id = g.guest_id
                WHERE u.username = ?
            )
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, joinedReservationIds);
            ps.setString(2, username);
            ps.executeUpdate();
        }
    }

    public void refreshGuestReservationIds() throws SQLException {
        // Reservation ownership is stored directly on the guest row as resId.
    }

    public List<String> findReservationIdsByGuestId(int guestId) throws SQLException {
        String sql = """
            SELECT resId
            FROM guests
            WHERE guest_id = ?
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, guestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return List.of();
                }
                return parseReservationIds(rs.getString("resId"));
            }
        }
    }

    public boolean guestOwnsReservation(int guestId, String reservationId) throws SQLException {
        return findReservationIdsByGuestId(guestId).stream()
                .anyMatch(id -> id.equalsIgnoreCase(reservationId));
    }

    private List<String> parseReservationIds(String rawReservationIds) {
        if (rawReservationIds == null || rawReservationIds.isBlank()) {
            return List.of();
        }

        List<String> reservationIds = new ArrayList<>();
        for (String reservationId : rawReservationIds.split(",")) {
            String normalizedReservationId = reservationId.trim();
            if (!normalizedReservationId.isEmpty()) {
                reservationIds.add(normalizedReservationId);
            }
        }
        return reservationIds;
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
}
