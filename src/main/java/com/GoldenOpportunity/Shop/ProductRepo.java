package com.GoldenOpportunity.Shop;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepo {
    private String dbURL;

    public ProductRepo(String dbURL) {
        this.dbURL = dbURL;
    }

    public List<Product> getAvailableProducts() {
        List<Product> products = new ArrayList<>();

        String sql = "SELECT product_id, name, price, amount_in_stock, description, image_path " +
                "FROM products " +
                "WHERE amount_in_stock > 0";

        try (Connection connection = DriverManager.getConnection(dbURL);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getInt("product_id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("amount_in_stock"),
                        resultSet.getString("description"),
                        resultSet.getString("image_path")
                );

                products.add(product);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("error getting available products", e);
        }

        return products;
    }

    public Product findProductById(int productID) {
        String sql = "SELECT product_id, name, price, amount_in_stock, description, image_path " +
                "FROM products " +
                "WHERE product_id = ?";

        try (Connection connection = DriverManager.getConnection(dbURL);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, productID);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Product(
                            resultSet.getInt("product_id"),
                            resultSet.getString("name"),
                            resultSet.getDouble("price"),
                            resultSet.getInt("amount_in_stock"),
                            resultSet.getString("description"),
                            resultSet.getString("image_path")
                    );
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("error finding product by id", e);
        }

        return null;
    }
}
