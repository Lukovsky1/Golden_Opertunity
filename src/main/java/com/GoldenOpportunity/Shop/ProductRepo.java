package com.GoldenOpportunity.Shop;

import com.GoldenOpportunity.DBUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// this file is used to work w all the SQL stuff
public class ProductRepo {
    private String dbURL;

    public ProductRepo(String dbURL) {
        this.dbURL = dbURL;
    }

    public List<ProductDescription> getAllProductDescriptions() {
        List<ProductDescription> productDescriptions = new ArrayList<>();

        String sql = "SELECT price, name, productID, image, description FROM products";

        try (Connection connection = DBUtil.getConnection(dbURL);
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

        try (Connection connection = DBUtil.getConnection(dbURL);
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