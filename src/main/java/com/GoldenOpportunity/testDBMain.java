package com.GoldenOpportunity;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class testDBMain {
    static RoomLoader roomLoader = new RoomLoader();
    static ReservationLoader reservationLoader = new ReservationLoader();

    public static void main (String[] args) {
        //roomLoader.createTable();
        //roomLoader.loadData();
        RoomService roomService = new RoomService();

       roomService.createTable();
        try {
            roomService.loadRoomsFromCSV("src/main/resources/testRoomData1.csv");
        } catch (IOException e) {
            System.err.println("Error loading rooms from file");
        }
        ReservationService reservationService = new ReservationService();


        //reservationLoader.createTable();
        //reservationLoader.loadData();


        List<Room> newRooms = roomService.getAllRooms();
        newRooms.remove(3);
        newRooms.remove(5);

        reservationService.createReservation(newRooms, LocalDate.now(),
                LocalDate.parse("2027-11-20"), 1000.0);

        //reservationService.deleteReservation("R-023");
        try {
            //Print reservation with ID R-024
            System.out.println(reservationService.findReservation("R-024"));

        } catch (SQLException e) {
            System.err.println("Error finding reservation: " + e.getMessage());
        }
    }


}
