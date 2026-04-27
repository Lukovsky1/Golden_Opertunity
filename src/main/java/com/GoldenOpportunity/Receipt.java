package com.GoldenOpportunity;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Receipt {
    private double total;
    private double penalty;
    private int guestID;
    private String resID;
    private String billingAddress;
    private Map<Integer, Double> roomBill;
    private Map<String, Double> shopItemBill;

    Receipt(){
        total = 0.0;
        penalty = 0.0;
        guestID = 0;
        resID = null;
        billingAddress = null;
        roomBill = new HashMap<>();
        shopItemBill = new HashMap<>();
    }

    public void addOnTotal(double amount){
        total = total + amount;
    }

    public Double calculateTotal() throws SQLException {
        total = penalty + total;

        if(roomBill.isEmpty()){
            SearchController searchController = new SearchController(new RoomService(), new ReservationService());
            Reservation reservation = searchController.getResService().findReservation(resID);
            for(Room room: reservation.getRooms()){
                addRoomToBill(room.getRoomNo(), room.getRate());
            }
        }

        for(Map.Entry<Integer, Double> entry: roomBill.entrySet()){
            total = total + entry.getValue();
        }
        for(Map.Entry<String, Double> entry: shopItemBill.entrySet()){
            total = total + entry.getValue();
        }
        return total;
    }

    public boolean addRoomToBill(int roomNo, double rate){
        if(roomBill.containsKey(roomNo)){
            return false;
        }
        roomBill.put(roomNo, rate);
        return true;
    }

    //Needs to be implemented
    public boolean addShopItemToBill(String name, double price){
        shopItemBill.put(name, price);
        return true;
    }

    public double getPenalty() {return penalty;}
    public int getGuestID() {return guestID;}
    public String getResID() {return resID;}
    public String getBillingAddress() {return billingAddress;}
    public Map<Integer, Double> getRoomBill() {return roomBill;}
    public Map<String, Double> getShopItemBill() {return shopItemBill;}

    public void setTotal(double total) {this.total = total;}
    public void setPenalty(double penalty) {this.penalty = penalty;}
    public void setGuestID(int guestID) {this.guestID = guestID;}
    public void setResID(String resID) {this.resID = resID;}
    public void setBillingAddress(String billingAddress) {this.billingAddress = billingAddress;}
    public void setRoomBill(Map<Integer, Double> roomBill) {this.roomBill = roomBill;}
    public void setShopItemBill(Map<String, Double> shopItemBill) {this.shopItemBill = shopItemBill;}
}
