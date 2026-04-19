package com.GoldenOpportunity;


import java.util.List;

//TODO: Must implement proper billing functionality
public class Reservation {
    final String resId;
    private List<Room> rooms;
    final DateRange dateRange;
    final double bill;


    Reservation(String resId, List<Room> rooms, DateRange dateRange, double bill) {
        this.resId = resId;
        this.rooms = rooms;
        this.dateRange = dateRange;
        this.bill =  bill;
    }
    public String getId() {
        return resId;
    }
    public List<Room> getRooms() {
        return rooms;
    }
    public DateRange getDateRange() {
        return dateRange;
    }
    public double getBill() {
        return bill;
    }



    @Override
    public String toString() {
        return "Reservation: " + resId + " " +  rooms + ", " + dateRange + ", " + bill;
    }

}
