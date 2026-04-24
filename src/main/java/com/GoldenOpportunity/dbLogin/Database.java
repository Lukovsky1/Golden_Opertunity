package com.GoldenOpportunity.dbLogin;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

/**
 * Guest must have their reservations either in the User Database or a separate database
 */

/**
 * Responsibilities:
 * - Define where the database file lives on disk (relative path under {@code data/}).
 * - Provide a JDBC {@link Connection} configured with sane defaults (e.g., foreign keys ON).
 * - Ensure the required schema exists the first time the app runs.
 *
 * Why this class exists: keeping JDBC URL and schema creation in one place makes it easier
 * to migrate or swap implementations later (e.g., move to SQLCipher, replace SQLite, etc.).
 */
public final class Database {
    /** Folder that contains the SQLite file. Relative to the project run directory. */
    private static final String DB_DIR = "src/main/resources"; //TODO: This was edited from data
    /** Database filename. The full path will be {@code data/golden.db}. */
    private static final String DB_FILE = "golden.db";
    /** JDBC URL for the SQLite driver. */
    private static final String JDBC_URL = "jdbc:sqlite:" + DB_DIR + "/" + DB_FILE;

    private Database() {}

    /**
     * Opens a connection to the SQLite database.
     *
     * Notes:
     * - SQLite supports enabling foreign key constraints per connection; we turn them on here.
     * - Callers are responsible for closing the returned connection.
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(JDBC_URL);
        // Enforce referential integrity for this connection.
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }

    /**
     * Ensures the database folder exists and creates the schema if missing.
     * Intended to be called once at application startup.
     */
    public static void initialize() throws SQLException {
        ensureDbFolder();
        // Opening a connection will also create the file on first use.
        try (Connection conn = getConnection()) {
            createSchema(conn);
        }
    }

    /** Create the data folder and an empty DB file path if needed. */
    private static void ensureDbFolder() {
        try {
            Path dir = Paths.get(DB_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            // Touch the DB file path so that the location exists and is visible to the user.
            File dbFile = dir.resolve(DB_FILE).toFile();
            if (!dbFile.exists()) {
                dbFile.createNewFile();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to prepare database directory", e);
        }
    }

    /** Create tables needed for authentication if they do not already exist. */
    private static void createSchema(Connection conn) throws SQLException {
        // Users table stores credentials (hashed), role, and lockout metadata.
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

        String createRooms = """
                CREATE TABLE IF NOT EXISTS Rooms (
                    floorNum   INTEGER NOT NULL,
                    roomNo     INTEGER PRIMARY KEY,
                    numBeds    INTEGER NOT NULL,
                    smoking    BOOLEAN NOT NULL,
                    qLevel     TEXT NOT NULL,
                    roomType   TEXT NOT NULL,
                    rate       REAL NOT NULL,
                    bedTypes   TEXT NOT NULL
                );
                """;

        String createReservation = """
                CREATE TABLE IF NOT EXISTS Reservations (
                      resId TEXT PRIMARY KEY,
                      startDate TEXT NOT NULL,
                      endDate TEXT NOT NULL,
                      bill REAL NOT NULL
                  );

                """;

        String createReservedRooms = """
                CREATE TABLE IF NOT EXISTS ReservedRooms (
                      resId TEXT NOT NULL,
                      roomNo INTEGER NOT NULL,
                      floorNum INTEGER NOT NULL,
                      PRIMARY KEY (resId, roomNo, floorNum),
                      FOREIGN KEY (resId) REFERENCES Reservation(resId),
                      FOREIGN KEY (roomNo, floorNum) REFERENCES Rooms(roomNo, floorNum)
                );
                """;

        try (Statement st = conn.createStatement()) {
            st.execute(createUsers);
            st.execute(createUniqueEmailIndex);
            st.execute(createRooms);
            st.execute(createReservation);
            st.execute(createReservedRooms);
        }
    }
}
