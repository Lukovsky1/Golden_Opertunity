package com.GoldenOpportunity.Shop;

public class ProductInventory {
    private int productID;
    private int stock;

    public ProductInventory(int productID, int stock) {
        this.productID = productID;
        this.stock = stock;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getProductID() {
        return productID;
    }

    public int getStock() {
        return stock;
    }

    // moved from original product
    public boolean isInStock() {
        return stock > 0;
    }

    public String getAvailability() {
        if (isInStock()) {
            return "In Stock";
        }

        return "Out of Stock";
    }
}
