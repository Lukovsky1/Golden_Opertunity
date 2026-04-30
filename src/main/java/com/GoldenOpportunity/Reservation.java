package com.GoldenOpportunity;


import java.util.List;

//TODO: Must implement proper billing functionality
public class Reservation {
    final String resId;
    private List<Room> rooms;
    final DateRange dateRange;
    final double bill;
    boolean checkedIn = false;
    String name;//A reservation is not checked in automatically


    Reservation(String resId, List<Room> rooms, DateRange dateRange, double bill, boolean checkedIn, String name) {
        this.resId = resId;
        this.rooms = rooms;
        this.dateRange = dateRange;
        this.bill =  bill;
        this.checkedIn = checkedIn;
        this.name = name;
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
    public boolean isCheckedIn() {
        return checkedIn;
    }
    public String getName(){return name;}



    @Override
    public String toString() {
        return "Reservation: " + resId + " " +  rooms + ", " + dateRange + ", " + bill + "; Checked in: " + checkedIn;
    }

}
