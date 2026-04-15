package com.GoldenOpportunity;

public class testDBMain {
    static RoomLoader roomLoader = new RoomLoader();

    public static void main (String[] args) {
        roomLoader.createTable();
        roomLoader.loadData();
    }
}
