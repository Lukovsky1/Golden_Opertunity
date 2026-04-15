package com.GoldenOpportunity.Shop;

public class Product {
    private int productID;
    private String name;
    private double price;
    private int amountInStock;

    // adding these 2 things cuz i think it could be useful
    private String description;
    private String imagePath;

    public Product(int productID, String name, double price, int amountInStock, String description, String imagePath) {
        this.productID = productID;
        this.name = name;
        this.price = price;
        this.amountInStock = amountInStock;
        this.description = description;
        this.imagePath = imagePath;
    }

    // im going to use a productRepo for the SQLite database, so i will add all of these
    // getters
    public int getProductID() {
        return productID;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getAmountInStock() {
        return amountInStock;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    // functions in the DCD:
    public boolean isInStock() {
        return amountInStock > 0;
    }

    // basic info for store page
    public String getSummary() {
        return "id: " + productID +
                ", name: " + name +
                ", price: $" + price +
                ", image: " + imagePath;
    }

    // detailed info for selected product page
    public String getDetails() {
        String availability = isInStock() ? "in stock" : "out of stock";

        return "id: " + productID +
                ", name: " + name +
                ", price: $" + price +
                ", description: " + description +
                ", availability: " + availability +
                ", stock amount: " + amountInStock +
                ", image: " + imagePath;
    }
}
