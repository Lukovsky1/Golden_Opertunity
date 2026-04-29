package com.GoldenOpportunity.DatabaseTools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBLoader {

    private DBLoader() {
        // Utility class — prevent instantiation
    }

    /**
     * Loads SQL statements from a .sql file and executes them sequentially.
     * Supports multiple statements separated by semicolons.
     *
     * @param conn      Active JDBC connection
     * @param filePath  Path to the .sql file
     */
    public static void loadData(Connection conn, String filePath, String table) throws SQLException, IOException {
        String load = Files.readString(Path.of(filePath));


        conn.setAutoCommit(false);

        try (Statement stmt = conn.createStatement()) {
            if (DBUtil.isTableEmpty(conn, table)) {
                stmt.executeUpdate(load);
            }
            // Execute the SQL statement

            conn.commit();

        } catch (SQLException ex) {
            conn.rollback();
            System.err.println("Error executing SQL file: " + filePath);
            throw ex;
        }
    }
}