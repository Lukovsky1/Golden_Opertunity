package com.GoldenOpportunity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Reservation {
    final String resId;
    final int roomNumber;
    final DateRange dateRange;
    final double bill;


    //TODO: Add ID as a field either to the test file or create them manually
    Reservation(String resId, int roomNumber, DateRange dateRange, double bill) {
        this.resId = resId;
        this.roomNumber = roomNumber;
        this.dateRange = dateRange;
        this.bill =  bill;
    }

    public String getId() {
        return resId;
    }
    public int getRoomNumber() {
        return roomNumber;
    }
    public DateRange getDateRange() {
        return dateRange;
    }
    public double getBill() {
        return bill;
    }



    @Override
    public String toString() {
        return "Reservation: " + resId + " " +  roomNumber + ", " + dateRange + ", " + bill;
    }

}
