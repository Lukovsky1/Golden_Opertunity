package com.GoldenOpportunity.Shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shop {
    private List<ProductDescription> productDescriptions;
    private Map<Integer, ProductInventory> inventoryMap;
    private static double profit;
    // handles SQL
    private ProductRepo productRepo;


    public Shop(ProductRepo productRepo) {
        this.productRepo = productRepo;
        this.productDescriptions = productRepo.getAllProductDescriptions();
        this.inventoryMap = new HashMap<>();

        for (ProductInventory inventory : productRepo.getAllProductInventory()) {
            inventoryMap.put(inventory.getProductID(), inventory);
        }
    }

    // gets all available productDescriptions
    public List<ProductDescription> getAvailableProducts() {
        List<ProductDescription> availableProductDescriptions = new ArrayList<>();

        for (ProductDescription productDescription : productDescriptions) {
            ProductInventory inventory = inventoryMap.get(productDescription.getProductID());

            if (inventory != null && inventory.isInStock()) {
                availableProductDescriptions.add(productDescription);
            }
        }

        return availableProductDescriptions;
    }

    // find product, note: returns null if not found
    public ProductDescription findProduct(int productID) {
        for (ProductDescription productDescription : productDescriptions) {
            if (productDescription.getProductID() == productID) {
                return productDescription;
            }
        }

        return null;
    }

    // new methods to get the productID to its stock
    public ProductInventory findInventory(int productID) {
        return inventoryMap.get(productID);
    }

    public boolean isInStock(int productID) {
        ProductInventory inventory = inventoryMap.get(productID);
        return inventory != null && inventory.isInStock();
    }

    public String getAvailability(int productID) {
        ProductInventory inventory = inventoryMap.get(productID);

        if (inventory == null) {
            return "Unavailable";
        }

        return inventory.getAvailability();
    }
}
