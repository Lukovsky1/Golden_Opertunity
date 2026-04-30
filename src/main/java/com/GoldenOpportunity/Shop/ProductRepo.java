package com.GoldenOpportunity.Shop;

import com.GoldenOpportunity.DatabaseTools.DBUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// this file is used to work w all the SQL stuff
//FIXME: Edited this file to not take a url and instead use the DBUtil.getConnection() url
public class ProductRepo {

    public ProductRepo() {}

    public List<ProductDescription> getAllProductDescriptions() {
        List<ProductDescription> productDescriptions = new ArrayList<>();

        String sql = "SELECT productID, name, price, image, description FROM ProductDescriptions";

        try (Connection connection = DBUtil.getConnection();
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

        String sql = "SELECT productID, stock FROM ProductDescriptions";

        try (Connection connection = DBUtil.getConnection();
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

    public boolean reduceStock(int productID, int quantity) {
        String sql = "UPDATE ProductDescriptions SET stock = stock - ? WHERE productID = ? AND stock >= ?";

        try (Connection connection = DBUtil.getConnection();
             java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, quantity);
            statement.setInt(2, productID);
            statement.setInt(3, quantity);

            int rowsUpdated = statement.executeUpdate();

            return rowsUpdated > 0;
        }
        catch (SQLException e) {
            throw new RuntimeException("error reducing product stock in database", e);
        }
    }
}