package com.GoldenOpportunity.DatabaseTools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class DBIntializer {
    private DBIntializer() {}

    public static void createSchema(Connection conn) throws SQLException {
        String createUsers = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password_hash TEXT NOT NULL,
                role TEXT NOT NULL,
                account_status TEXT NOT NULL DEFAULT 'ACTIVE',
                failed_login_count INTEGER NOT NULL DEFAULT 0,
                contact_info TEXT,
                created_at TEXT NOT NULL,
                updated_at TEXT NOT NULL
            );
        """;

        String createUniqueEmailIndex = """
            CREATE UNIQUE INDEX IF NOT EXISTS idx_users_contact_info_unique
            ON users(contact_info)
            WHERE contact_info IS NOT NULL AND contact_info <> '';
        """;

        String createGuests = """
            CREATE TABLE IF NOT EXISTS guests (
                guest_id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                email TEXT,
                resId TEXT,
                created_at TEXT NOT NULL,
                updated_at TEXT NOT NULL,
                FOREIGN KEY (guest_id) REFERENCES users(id) ON DELETE CASCADE
            );
        """;

        String createGuestReservationView = """
            CREATE VIEW IF NOT EXISTS guest_reservation_summary AS
            SELECT
                g.guest_id,
                g.name,
                g.email,
                g.resId
            FROM guests g
            ORDER BY g.guest_id;
        """;

        try (Statement st = conn.createStatement()) {
            st.execute(createUsers);
            st.execute(createUniqueEmailIndex);
            st.execute(createGuests);
            ensureGuestsResIdColumn(conn, st);
            migrateReservationIdsToResId(conn, st);
            st.execute("DROP INDEX IF EXISTS idx_reservations_guest_id");
            st.execute("DROP TABLE IF EXISTS reservations");
            st.execute("DROP VIEW IF EXISTS guest_reservation_summary");
            st.execute(createGuestReservationView);
        }
    }

    private static void ensureGuestsResIdColumn(Connection conn, Statement st) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getColumns(null, null, "guests", "resId")) {
            if (!rs.next()) {
                st.execute("ALTER TABLE guests ADD COLUMN resId TEXT");
            }
        }
    }

    private static void migrateReservationIdsToResId(Connection conn, Statement st) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getColumns(null, null, "guests", "reservation_ids")) {
            if (rs.next()) {
                st.execute("""
                    UPDATE guests
                    SET resId = COALESCE(resId, reservation_ids)
                    WHERE reservation_ids IS NOT NULL
                """);
            }
        }
    }
}
