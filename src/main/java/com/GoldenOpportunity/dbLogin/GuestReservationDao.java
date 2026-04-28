package com.GoldenOpportunity.dbLogin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

/**
 * Synchronizes guest users and reservation records into SQLite.
 *
 * Historical reservation CSV data in this project does not contain a guest identifier,
 * so imported records are preserved with a null guest_id until ownership is known.
 */
public class GuestReservationDao {
    private static final String SEEDED_RESERVATION_SOURCE = "SEEDED";

    public void initializeSchema() throws SQLException {
        Database.initialize();
    }

    public int syncGuestUsersFromUsersTable() throws SQLException {
        String sql = """
            INSERT INTO guests (guest_id, name, email, reservation_ids, created_at, updated_at)
            SELECT id, username, contact_info, NULL, created_at, updated_at
            FROM users
            WHERE role = 'GUEST'
            ON CONFLICT(guest_id) DO UPDATE SET
                name = excluded.name,
                email = excluded.email,
                updated_at = excluded.updated_at
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int synced = ps.executeUpdate();
            refreshGuestReservationIds();
            return synced;
        }
    }

    public int importReservationsFromCsv(Path csvPath) throws IOException, SQLException {
        List<String> lines = Files.readAllLines(csvPath);
        if (lines.size() <= 1) {
            return 0;
        }

        String sql = """
            INSERT INTO reservations (
                reservation_id, guest_id, room_number, start_date, end_date, bill, source, created_at, updated_at
            ) VALUES (?, NULL, ?, ?, ?, 0, 'CSV_IMPORT', ?, ?)
            ON CONFLICT(reservation_id) DO UPDATE SET
                room_number = excluded.room_number,
                start_date = excluded.start_date,
                end_date = excluded.end_date,
                bill = excluded.bill,
                source = excluded.source,
                updated_at = excluded.updated_at
        """;

        int imported = 0;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 4) {
                    continue;
                }

                String now = Instant.now().toString();
                ps.setString(1, parts[0].trim());
                ps.setInt(2, Integer.parseInt(parts[1].trim()));
                ps.setString(3, parts[2].trim());
                ps.setString(4, parts[3].trim());
                ps.setString(5, now);
                ps.setString(6, now);
                ps.addBatch();
                imported++;
            }
            ps.executeBatch();
        }

        return imported;
    }

    public void seedReservationForGuest(String username,
                                        String reservationId,
                                        int roomNumber,
                                        String startDate,
                                        String endDate,
                                        double bill) throws SQLException {
        Integer guestId = findGuestIdByUsername(username);
        if (guestId == null) {
            throw new IllegalStateException("Guest user not found for reservation seed: " + username);
        }

        String now = Instant.now().toString();
        String sql = """
            INSERT INTO reservations (
                reservation_id, guest_id, room_number, start_date, end_date, bill, source, created_at, updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(reservation_id) DO UPDATE SET
                guest_id = excluded.guest_id,
                room_number = excluded.room_number,
                start_date = excluded.start_date,
                end_date = excluded.end_date,
                bill = excluded.bill,
                source = excluded.source,
                updated_at = excluded.updated_at
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reservationId);
            ps.setInt(2, guestId);
            ps.setInt(3, roomNumber);
            ps.setString(4, startDate);
            ps.setString(5, endDate);
            ps.setDouble(6, bill);
            ps.setString(7, SEEDED_RESERVATION_SOURCE);
            ps.setString(8, now);
            ps.setString(9, now);
            ps.executeUpdate();
        }

        refreshGuestReservationIds();
    }

    public void refreshGuestReservationIds() throws SQLException {
        String sql = """
            UPDATE guests
            SET reservation_ids = (
                SELECT group_concat(reservation_id, ', ')
                FROM (
                    SELECT reservation_id
                    FROM reservations
                    WHERE guest_id = guests.guest_id
                    ORDER BY reservation_id
                )
            )
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }

    private Integer findGuestIdByUsername(String username) throws SQLException {
        String sql = """
            SELECT g.guest_id
            FROM guests g
            JOIN users u ON u.id = g.guest_id
            WHERE u.username = ?
        """;

        try (Connection conn = Database.getConnection();
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
