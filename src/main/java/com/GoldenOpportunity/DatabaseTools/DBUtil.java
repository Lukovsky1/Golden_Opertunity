package com.GoldenOpportunity.DatabaseTools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
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

    /** Create the data folder and an empty DB file path if needed. */
    public static void ensureDbFolder() {
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

    public static boolean isTableEmpty(Connection con, String table) throws SQLException {
        String givenTable = """
                SELECT COUNT(*) FROM
                """ + " " + table;
        try (Statement stmt = con.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(givenTable);
            return !resultSet.next() || resultSet.getInt(1) == 0;
        } catch (SQLException e) {
            System.err.println("Error while checking if table " + table +  " exists: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Given the desired table name, this function will return the number
     * of rows in the selected table.
     *
     * @param tableName
     * @return
     */
    public static int getCountOfAllInTable(String tableName) {
        String getCount = """
                SELECT COUNT (*) FROM
                """ +  tableName;
        try (Connection conn = DBUtil.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(getCount);
            return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error getting count of table: " + tableName + e.getMessage());
        }
        return 0;
    }

}
