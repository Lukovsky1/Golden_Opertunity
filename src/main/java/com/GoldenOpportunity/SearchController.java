package com.GoldenOpportunity;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//TODO: Rooms must be loaded first before the reservations
public class SearchController {
    private final RoomService roomService;
    private final ReservationService resService;
    public SearchController(RoomService roomService, ReservationService reservationService) {
        this.roomService = roomService;
        this.resService = reservationService;
    }


    /**
     * printRoomsAndReservations: Prints all rooms and any reservations assigned
     * to that room. Will print the reservation ids as a proxy for the
     * reservation object.
     */
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

            //Creating a new List<Room> for object for reservation for testing createReservation
            Room [] testRooms = {searchController.roomService.getRoomList().get(0),
                    searchController.roomService.getRoomList().get(1),
            searchController.roomService.getRoomList().get(4)};
            List<Room> newRooms = Arrays.asList(testRooms);

            //TODO: reserveRoom Use Case
            searchController.resService.createReservation(newRooms, LocalDate.now(),
                    LocalDate.parse("2026-11-20"), 0.0);
            searchController.resService.deleteReservation("R-019");

            Criteria criteria = new Criteria();
            criteria.setDateRange(null);
            //criteria.setRoomNum(101);
            criteria.setFloorNum(3);
            criteria.setSmoking(true);
            criteria.setRoomType("Deluxe");
            criteria.setDateRange( new DateRange(LocalDate.now(), LocalDate.now().plusDays(1)));


            searchController.printRoomsAndReservations();
        }catch (FileNotFoundException e){
            System.out.println("Room or Reservation File Not Found");
        }
    }


}
