package com.GoldenOpportunity.dbLogin;

import com.GoldenOpportunity.DatabaseTools.DBIntializer1;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
    //private static final String DB_DIR = "data";
    /** Database filename. The full path will be {@code data/golden.db}. */
    //private static final String DB_FILE = "golden.db";
    /** JDBC URL for the SQLite driver. */
    //private static final String JDBC_URL = "jdbc:sqlite:" + DB_DIR + "/" + DB_FILE;

    private Database() {}

    /**
     * Opens a connection to the SQLite database.
     *
     * Notes:
     * - SQLite supports enabling foreign key constraints per connection; we turn them on here.
     * - Callers are responsible for closing the returned connection.
     */
    /*public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(JDBC_URL);
        // Enforce referential integrity for this connection.
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    } */

    /**
     * Ensures the database folder exists and creates the schema if missing.
     * Intended to be called once at application startup.
     */
   /* public static void initialize() throws SQLException {
        ensureDbFolder();
        // Opening a connection will also create the file on first use.
        try (Connection conn = getConnection()) {
            DBIntializer1.createSchema(conn);
        }
    } */

    /** Create the data folder and an empty DB file path if needed. */
    /*private static void ensureDbFolder() {
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
    */
}
