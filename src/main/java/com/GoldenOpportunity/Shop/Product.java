package com.GoldenOpportunity.Shop;

public class Product {
    private double price;
    private int stock;
    private String name;
    private int productID;

    Product(double price,int stock,String name,int productID){
        this.price = price;
        this.stock = stock;
        this.name = name;
        this.productID = productID;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public String getName() {
        return name;
    }

    public int getProductID() {
        return productID;
    }
}
