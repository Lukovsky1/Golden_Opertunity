package com.GoldenOpportunity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//TODO: Must be implemented
public class DBUtil {
    private static String url = "jdbc:sqlite:Hotel.db";

    public DBUtil() {
    }

    public static void setUrl(String newUrl) {
        url = newUrl;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

}
