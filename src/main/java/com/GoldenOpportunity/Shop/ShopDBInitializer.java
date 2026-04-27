package com.GoldenOpportunity.Shop;

import com.GoldenOpportunity.DatabaseTools.DBUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// this file is to load the shop database or intialize it
public class ShopDBInitializer {
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/shop.db";
    private static final String SQL_FILE_PATH = "src/main/resources/shop.sql";

    public static void initializeDatabase() {
        try (Connection connection = DBUtil.getConnection();
             Statement statement = connection.createStatement()) {

            // create the table if it does not already exist
            String createTableSql = """
                    CREATE TABLE IF NOT EXISTS products (
                        productID INTEGER PRIMARY KEY,
                        name TEXT NOT NULL,
                        price REAL NOT NULL,
                        stock INTEGER NOT NULL,
                        image TEXT NOT NULL,
                        description TEXT
                    );
                    """;

            statement.execute(createTableSql);
            // put starter data if empty
            if (isProductsTableEmpty(connection)) {
                runInsertStatements(connection);
                System.out.println("shop database filled successfully");
            }
            else {
                System.out.println("shop database already has data, skipping filler");
            }

        } catch (SQLException e) {
            throw new RuntimeException("failed to initialize shop database", e);
        }
    }

    private static boolean isProductsTableEmpty(Connection connection) throws SQLException {
        String countSql = "SELECT COUNT(*) FROM products";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(countSql)) {

            return !resultSet.next() || resultSet.getInt(1) == 0;
        }
    }

    private static void runInsertStatements(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            String sqlScript = Files.readString(Path.of(SQL_FILE_PATH));

            String[] sqlStatements = sqlScript.split(";");

            for (String sql : sqlStatements) {
                sql = sql.trim();

                // skip blanks and skip create table since we already handled that above
                if (sql.isEmpty() || sql.toUpperCase().startsWith("CREATE TABLE")) {
                    continue;
                }

                statement.executeUpdate(sql);
            }

        } catch (IOException e) {
            throw new RuntimeException("failed to read shop.sql", e);
        } catch (SQLException e) {
            throw new RuntimeException("failed to seed shop database", e);
        }
    }
}