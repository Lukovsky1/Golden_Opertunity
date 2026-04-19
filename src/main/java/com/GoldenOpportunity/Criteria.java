package com.GoldenOpportunity;

import java.util.HashMap;
import java.util.Map;

//TODO: Add validation functuions to the criteria (ex: floorNum > 0 && <= 3)
//TODO: Smoking may be extraneous because we have floor num
public class Criteria {
    private int floorNum;
    private int roomNum;
    private String roomType;
    private String quality;
    private Map<String, Integer> beds;
    private int numBeds;
    private double rate;
    boolean smoking;
    DateRange dateRange;


    Criteria() {
        floorNum = 0;
        roomNum = 0;
        roomType = "";
        quality = "";
        beds = new HashMap<>();
        numBeds = 0;
        rate = 0;
        smoking = false;
        dateRange = null;
    }

    public int getFloorNum() {return floorNum;}
    public int getRoomNum() { return roomNum; }
    public int getNumBeds() { return numBeds; }
    public boolean isSmoking() { return smoking; }
    public String getQuality() { return quality ; }
    public String getRoomType() { return roomType; }
    public double getRate() { return rate; }
    public DateRange getDateRange() {
        return dateRange;
    }
    public Map<String, Integer> getBeds(){return beds;}

    public void setFloorNum(int floorNum) {this.floorNum = floorNum;}
    public void setRoomNum(int roomNum) { this.roomNum = roomNum; }
    public void setNumBeds(int numBeds) { this.numBeds =  numBeds; }
    public void setSmoking(boolean isSmoking) { this.smoking =  isSmoking; }
    public void setQuality(String quality) { this.quality =  quality ; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public void setRate(double rate) { this.rate = rate; }
    public void setDateRange(DateRange dateRange) { this.dateRange = dateRange; }
    public void setBeds(Map<String, Integer> beds){this.beds.putAll(beds);}
}
