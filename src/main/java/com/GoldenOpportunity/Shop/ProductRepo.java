package com.GoldenOpportunity.Shop;

import com.GoldenOpportunity.DatabaseTools.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// this file is used to work w all the SQL stuff
//FIXME: Edited this file to not take a url and instead use the DBUtil.getConnection() url
public class ProductRepo {

    public ProductRepo() {}

    public List<ProductDescription> getAllProductDescriptions() {
        List<ProductDescription> productDescriptions = new ArrayList<>();

        String sql = "SELECT productID, name, price, image, description FROM products";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/shop.db");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                ProductDescription productDescription = new ProductDescription(
                        resultSet.getDouble("price"),
                        resultSet.getString("name"),
                        resultSet.getInt("productID"),
                        resultSet.getString("image"),
                        resultSet.getString("description")
                );

                productDescriptions.add(productDescription);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("error loading product descriptions from database", e);
        }

        return productDescriptions;
    }

    public List<ProductInventory> getAllProductInventory() {
        List<ProductInventory> inventoryList = new ArrayList<>();

        String sql = "SELECT productID, stock FROM products";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/shop.db");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                ProductInventory inventory = new ProductInventory(
                        resultSet.getInt("productID"),
                        resultSet.getInt("stock")
                );

                inventoryList.add(inventory);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("error loading product inventory from database", e);
        }

        return inventoryList;
    }
}