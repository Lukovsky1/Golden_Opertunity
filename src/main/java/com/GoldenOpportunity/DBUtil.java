package com.GoldenOpportunity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static String url = "jdbc:sqlite:src/main/resources/hotel.db";

    DBUtil() {
    }

    public static void setUrl(String newUrl) {
        url = newUrl;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }
}
