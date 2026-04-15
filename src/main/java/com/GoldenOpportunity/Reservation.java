package com.GoldenOpportunity;


import java.util.List;

//TODO: Must implement proper billing functionality
public class Reservation {
    final String resId;
    private Room room;
    final DateRange dateRange;
    final double bill;


    //TODO: Add ID as a field either to the test file or create them manually
    Reservation(String resId, Room room, DateRange dateRange, double bill) {
        this.resId = resId;
        this.room = room;
        this.dateRange = dateRange;
        this.bill =  bill;
    }

    public String getId() {
        return resId;
    }
    public Room getRoom() {
        return room;
    }
    public DateRange getDateRange() {
        return dateRange;
    }
    public double getBill() {
        return bill;
    }



    @Override
    public String toString() {
        return "Reservation: " + resId + " " +  room + ", " + dateRange + ", " + bill;
    }

}
