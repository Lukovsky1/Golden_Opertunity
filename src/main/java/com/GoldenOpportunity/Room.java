package com.GoldenOpportunity;


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
