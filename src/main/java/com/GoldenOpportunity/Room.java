package com.GoldenOpportunity;

import java.util.HashMap;
import java.util.Map;

/**
 * Edits: Added floorNum to the class
 */
public class Room {
    int floorNum;
    int roomNo;
    int beds;
    Map<String, Integer> bedTypes;
    boolean smoking;
    String qLevel;
    String roomType;
    double rate;

    public Room(int floorNum, int rmNo, int numBeds, boolean smoke, String qlty,
                String rmType, double rate, Map<String, Integer> bedTypesInput) {

        this.floorNum = floorNum;
        this.roomNo = rmNo;
        this.beds = numBeds;
        this.smoking = smoke;
        this.qLevel = qlty;
        this.roomType = rmType;
        this.rate = rate;

        this.bedTypes = new HashMap<>();
        this.bedTypes.putAll(bedTypesInput);
    }


    public int getFloorNum() {return floorNum;}
    public int getRoomNo() { return roomNo; }
    public int getBeds() { return beds; }
    public boolean isSmoking() { return smoking; }
    public String getQLevel() { return qLevel; }
    public String getRoomType() { return roomType; }
    public double getRate() { return rate; }
    public Map<String, Integer> getBedTypes(){return bedTypes;}

    public void setBeds(int beds) { this.beds = beds; }
    public void setSmoking(boolean smoking) { this.smoking = smoking; }
    public void setQLevel(String qLevel) { this.qLevel = qLevel; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public void setRate(double rate) { this.rate = rate; }
    public void setBedTypes(Map<String, Integer> bedTypeInput){bedTypes.putAll(bedTypeInput);}

    //String [] bedTypes = {"King", "Queen", "Twin", "Full"};


    /*public enum QualityLevel{
        Economy, Comfort, Business, Executive;
    } */

    @Override
    public String toString(){
        return roomNo + " NumBeds: " + beds + " Quality: " + qLevel + " Type: " + roomType + " Rate: " + rate;
    }
}

/*
public class Room {
    int roomNo;
    int beds;
    boolean smoking;
    String qLevel;
    String roomType;
    double rate;

    Room(int rmNo, int b, boolean sm, String qlty, String rmType, double r){
        roomNo = rmNo;
        beds = b;
        smoking = sm;
        qLevel = qlty;
        roomType = rmType;
        rate = r;
    }

    public int getRoomNo() { return roomNo; }
    public int getBeds() { return beds; }
    public boolean isSmoking() { return smoking; }
    public String getQLevel() { return qLevel; }
    public String getRoomType() { return roomType; }
    public double getRate() { return rate; }

    public void setBeds(int beds) { this.beds = beds; }
    public void setSmoking(boolean smoking) { this.smoking = smoking; }
    public void setQLevel(String qLevel) { this.qLevel = qLevel; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public void setRate(double rate) { this.rate = rate; }

}
*/