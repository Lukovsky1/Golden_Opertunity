package com.GoldenOpportunity.DatabaseTools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
//TODO: Must be implemented

public class DBUtil {
    private static final String DB_DIR = "src/main/resources"; //TODO: This was edited from /data
    /** Database filename. The full path will be {@code data/golden.db}. */
    private static final String DB_FILE = "golden.db";
    /** JDBC URL for the SQLite driver. */
    private static final String JDBC_URL = "jdbc:sqlite:" + DB_DIR + "/" + DB_FILE;

    //private static String url = "jdbc:sqlite:src/main/resources/golden.db";

    public DBUtil() {
    }

    /**
     * Opens a connection to the SQLite database.
     *
     * Notes:
     * - SQLite supports enabling foreign key constraints per connection; we turn them on here.
     * - Callers are responsible for closing the returned connection.
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(JDBC_URL);
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }


}
