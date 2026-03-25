package com.GoldenOpportunity;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

//TODO: Rooms must be loaded first before the reservations
public class SearchController {
    //TODO: Edit, should be private
    private final RoomService roomService;
    private final ReservationService resService;
    public SearchController(RoomService roomService, ReservationService reservationService) {
        this.roomService = roomService;
        this.resService = reservationService;
    }


    //Will print all rooms and any reservations (ids) assigned to those rooms
    public void printRoomsAndReservations() {
        for (int i = 0; i < roomService.getRoomList().size(); i++) {
            int num = roomService.getRoomList().get(i).getRoomNo();
            System.out.println("All reservations assigned to " + num);
            for (Reservation r : resService.getReservations()) {
                if (r.getRooms().contains(roomService.getRoomList().get(i))) {
                    System.out.println(r.getId());
                }
            }
        }
    }

    /*
    Testing and debug note: DO NOT use "new" when trying to create room objects!
    They will create new objects in memory that, even if ostensibly identical,
    are in different places in memory and are not equivalent to each other.
     */
    public static void main(String [] args) {
        try {
            SearchController searchController = new SearchController(new RoomService
                    ("src/main/resources/testRoomData1.csv"),
                    new ReservationService
                            (Path.of("src/main/resources/testReservationData1.csv")));

            //Creating a new room for reservation for testing createReservation
            Room [] testRooms = {searchController.roomService.getRoomList().get(0),
                    searchController.roomService.getRoomList().get(1),
            searchController.roomService.getRoomList().get(4)};
            List<Room> newRooms = Arrays.asList(testRooms);

            //TODO: Create Reservation Use Case
            searchController.resService.createReservation(newRooms, LocalDate.now(), LocalDate.now(), 0.0);
            searchController.resService.deleteReservation("R-019");

            searchController.printRoomsAndReservations();
        }catch (FileNotFoundException e){
            System.out.println("Room or Reservation File Not Found");
        }
    }


}
