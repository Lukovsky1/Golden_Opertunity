package com.GoldenOpportunity.Shop;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private static int nextOrderID = 1;

    private int orderID;
    private LocalDate orderDate;
    private double totalAmount;
    private String status;
    private List<ProductDescription> orderItems;
    private int guestID;

    public Order() {
        this.orderItems = new ArrayList<>();
    }

    public void createOrder(List<ProductDescription> cartItems, int guestID, double total) {
        this.orderID = nextOrderID++;
        this.orderDate = LocalDate.now();
        this.totalAmount = total;
        this.status = "Created";
        this.guestID = guestID;
        this.orderItems = new ArrayList<>(cartItems);
    }

    public int getOrderID() {
        return orderID;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public List<ProductDescription> getOrderItems() {
        return orderItems;
    }

    public int getGuestID() {
        return guestID;
    }
}
