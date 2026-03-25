package com.GoldenOpportunity;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

public class ReservationService {
    private List<Reservation> reserveList = new ArrayList<>();
    //ReservationId mapped to its reservation object
    private Map<String, Reservation> reservationMap = new HashMap<>();
    private final Set<Integer> ValidIDNums = new HashSet<>();

    //Used to read CSV files of the form of the Reservation file

    /**
     * ReservationService constructor: Calls loadData which will fill reserveList
     * reservationMap, and ValidIDNums. Takes the file path to a relevant data file
     * as an argument.
     *
     * @param filePath
     */
    public ReservationService(Path filePath) {
        loadData(filePath);
    }

    /**
     * loadData:
     * Given a filepath, will load all relevant data into reserveList,
     * and reservationMap. Calls the ReservationLoader.loadReservations
     * method to achieve this. Also will fill all validIDNums for use
     * in creating and deleting future reservations.
     * @param filePath
     */
    public void loadData(Path filePath) {
        reserveList.clear(); //May need to be edited, currently assigned for testing
        try {
            ReservationLoader loader = new ReservationLoader();
            reserveList = loader.loadReservations(filePath);
            for (Reservation r : reserveList) {
                reservationMap.put(r.getId().toUpperCase(), r);
            }
            fillValidIDNums();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading reservations from file: " + e.getMessage());
        }

    }

    /**
     * //TODO: IF there are more than 999 reservations, how do we handle that edge case.
     * createReservation: Will create a new reservation and place it in both reserveList
     * and reservationMap.
     * @param rooms
     * @param start
     * @param end
     * @param bill
     */
    public void createReservation(List<Room> rooms , LocalDate start, LocalDate end, double bill) {
        //Creates a new reservation ID from the set of all valid reservation ids
        String newResId = createResId();
        //Confirms that the two given dates are within a valid range
        if (!DateRange.validateRange(start, end)) {
            System.out.println("Invalid date range");
            return;
        }
        Reservation newRes = new Reservation(newResId, rooms, new DateRange(start, end), bill);
        reserveList.add(newRes);
        try {
            assert newResId != null;
            reservationMap.put(newResId.toUpperCase(), newRes);
        }
        catch (NullPointerException e) {
            System.err.println("Can not put reservation into map: " + e.getMessage());
        }
    }

    //TODO: Must be able to write to the database/file and remove/add reservations
    //TODO: Possibly remove try/catch statement
    public void deleteReservation(String reservationId) {
        try {
            if (findReservationByID(reservationId) != null) {
                reserveList.remove((findReservationByID(reservationId)));
                reservationMap.remove(reservationId);
                ValidIDNums.remove(Integer.parseInt(reservationId.substring( 2)));
            }
            else {
                System.out.println("Reservation ID not found: " + reservationId);
            }
        }
        catch (NoSuchElementException e) {
            e.printStackTrace();
            System.out.println("Delete: Reservation not found: " + e.getMessage());
        }
    }

    /**
     * TODO: Must test if there are over 999 reservations
     * @return The new reservationID with the smallest value of the
     * reservation. If there are no valid numbers left, the function will
     * return null.
     */
    private String createResId() {
        int num = -1;
        int start = 1;
        int end = 999;

        for (int i = start; i <= end && num == -1; i++) {
            if (!ValidIDNums.contains(i)) {
                num = i;
                ValidIDNums.add(i);
            }
        }
        if (num == -1) {
            return null;
        }
        return String.format("R-%03d", num);
    }

    /**
     * fillValidIDNums: Will do a preliminary fill of the set of all valid ID numbers.
     */
    private void fillValidIDNums() {
        ValidIDNums.clear();
        for (int i = 0; i < reserveList.size(); i++) {
            Integer num = Integer.parseInt(reserveList.get(i).getId().substring(2));
            ValidIDNums.add(num);
        }
    }



    /**
     * findReservationByID: Searches for a reservation via it's ID
     * @param reservationId ID of the sought after reservation
     * @return the Reservation object if it exists, null if otherwise
     */
    public Reservation findReservationByID(String reservationId) {
        if (reservationMap.containsKey(reservationId)) {
            return reservationMap.get(reservationId.toUpperCase());
        }
        return null;
    }

    public List<Reservation> getReservations() {
        return reserveList;
    }

    public Map<String, Reservation> getReservationMap() {
        return reservationMap;
    }

    //TODO: Delete test
    /*public static void main (String[] args) {
        ReservationService reservationService =
        reservationService.loadData(Path.of("src/main/resources/testReservationData1.csv"));

        Room [] testRooms = {new Room(101, 2, false, "Executive", "Double", 100),
        new Room(102, 1, false, "Comfort", "Single", 150),
        new  Room(103, 4, false, "Economic", "Family", 150)};
        List<Room> newRooms= Arrays.asList(testRooms);





        reservationService.deleteReservation("R-001");
        reservationService.createReservation(newRooms, LocalDate.now(), LocalDate.now(), 0.5);
        reservationService.getReservations().forEach(System.out::println);
        Scanner scanner = new Scanner(System.in);
        String id =  scanner.nextLine();
        System.out.println("Reservation ID: " + id);
        System.out.println("Reservation: " + reservationService.findReservationByID(id));
    } */
}
