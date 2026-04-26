package com.GoldenOpportunity;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.GoldenOpportunity.DatabaseTools.DBInitializer;

import static java.lang.System.exit;

public class testDBMain {

    static {
        try {
            DBInitializer.initialize();
        } catch (SQLException e) {
            System.err.println("Error initializing DB: Quitting program");
            exit(1);
        } catch (IOException e) {
            System.err.println("Error reading DB Files: Quitting program");
            exit(1);
        }
    }
    static RoomLoader roomLoader = new RoomLoader();
    static ReservationLoader reservationLoader = new ReservationLoader();
    static ReservationService reservationService = new ReservationService();
    static RoomService roomService = new RoomService();

    public static void main (String[] args) {
        try {
            DBInitializer.initialize();
        } catch (SQLException ex) {
            System.err.println("Error initializing DB");
            throw new RuntimeException(ex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        //roomLoader.createTable();
        //roomLoader.loadData();
        /*RoomService roomService = new RoomService();

       roomService.createTable();
        try {
            roomService.loadRoomsFromCSV("src/main/resources/testRoomData1.csv");
        } catch (IOException e) {
            System.err.println("Error loading rooms from file");
        }
        ReservationService reservationService = new ReservationService();


        reservationLoader.createTable();
        reservationLoader.loadData();

*/
        /*List<Room> newRooms = roomService.getAllRooms();
        newRooms.remove(10);
        newRooms.remove(5);

        reservationService.createReservation(newRooms, LocalDate.now(),
                LocalDate.parse("2027-11-20"), 1000.0);

        /*reservationService.deleteReservation("R-027");
        reservationService.deleteReservation("R-024");*/

        //reservationService.deleteReservation("R-023");
        try {
            //Print reservation with ID R-024
            System.out.println(reservationService.findReservation("R-024"));

        } catch (SQLException e) {
            System.err.println("Error finding reservation: " + e.getMessage());
        }
    }

}
