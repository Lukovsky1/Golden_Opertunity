package com.GoldenOpportunity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//TODO: Add validation functions to the criteria (ex: floorNum > 0 && <= 3)
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
    int capacity;
    String description;
    String image;


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
        capacity = 0;
        description = "";
        image = "";
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
    public int getCapacity() {return capacity;}
    public String getDescription() {return description;}
    public String getImage() {return image;}

    public void setFloorNum(int floorNum) {this.floorNum = floorNum;}
    public void setRoomNum(int roomNum) { this.roomNum = roomNum; }
    public void setNumBeds(int numBeds) { this.numBeds =  numBeds; }
    public void setSmoking(boolean isSmoking) { this.smoking =  isSmoking; }
    public void setQuality(String quality) { this.quality =  quality ; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public void setRate(double rate) { this.rate = rate; }
    public void setDateRange(DateRange dateRange) { this.dateRange = dateRange; }
    public void setBeds(Map<String, Integer> beds){this.beds.putAll(beds);}
    public void setCapacity(int capacity) {this.capacity = capacity;}
    public void setDescription(String description) {this.description = description;}
    public void setImage(String image) {this.image = image;}
}