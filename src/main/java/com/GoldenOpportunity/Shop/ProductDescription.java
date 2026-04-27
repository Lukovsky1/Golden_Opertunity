package com.GoldenOpportunity.Shop;

public class ProductDescription {
    private double price;
    private String name;
    private int productID;
    private String image;
    // adding description
    private String description;

    public ProductDescription(double price, String name, int productID, String image, String description){
        this.price = price;
        this.name = name;
        this.productID = productID;
        this.image = image;
        this.description = description;
    }

    public void setPrice(double price) {this.price = price;}
    public void setName(String name) {this.name = name;}
    public void setProductID(int productID) {this.productID = productID;}
    public void setImage(String image){this.image = image;}
    public void setDescription(String description){this.description = description;}

    public double getPrice() {return price;}
    public String getName() {return name;}
    public int getProductID() {return productID;}
    public String getImage(){return image;}
    public String getDescription(){return description;}

    // new functions

    // basic summary
    public String getSummary() {
        return "Product status ID: " + productID +
                ", Name: " + name +
                ", Price: $" + price +
                ", Image: " + image;
    }

    // more in depth
    public String getDetails() {

        return "Product ID: " + productID +
                ", Name: " + name +
                ", Price: $" + price +
                ", Description: " + description +
                ", Image: " + image;
    }
}
