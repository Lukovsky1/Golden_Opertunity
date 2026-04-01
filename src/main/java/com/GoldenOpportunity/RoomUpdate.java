package com.GoldenOpportunity;

public class RoomUpdate {
    Integer beds;
    Boolean smoking;
    String qLevel;
    String roomType;
    Double rate;

    public RoomUpdate setBeds(Integer beds) {
        this.beds = beds;
        return this;
    }

    public RoomUpdate setSmoking(Boolean smoking) {
        this.smoking = smoking;
        return this;
    }

    public RoomUpdate setQLevel(String qLevel) {
        this.qLevel = qLevel;
        return this;
    }

    public RoomUpdate setRoomType(String roomType) {
        this.roomType = roomType;
        return this;
    }

    public RoomUpdate setRate(Double rate) {
        this.rate = rate;
        return this;
    }
}
