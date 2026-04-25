package com.GoldenOpportunity;

import com.GoldenOpportunity.DatabaseTools.DBUtil;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

//TODO: Add checkedIn checking for the database
//TODO: Add getAllReservations();
//TODO: Make reservation take a randomly generated id, a string seems too extra and
//TODO: unnecessary

public class ReservationService {
    //TODO: Remove
    private List<Reservation> reserveList = new ArrayList<>();
    private ReservationLoader reservationLoader =  new ReservationLoader();
    //ReservationId mapped to its reservation object
    private Map<String, Reservation> reservationMap = new HashMap<>();
    //Set of ID numbers in use. Used to find the smallest unused ID value
    private final Set<Integer> ValidIDNums = new HashSet<>();

    //Used to read CSV files of the form of the Reservation file

    /**
     * ReservationService constructor: Calls loadData which will fill reserveList
     * reservationMap, and ValidIDNums. Takes the file path to a relevant data file
     * as an argument.
     */
    public ReservationService() {
        //reservationLoader.createTable(); //TODO: Delete
        try {
            fillValidIDNums();
        } catch (SQLException e) {
            System.out.println("Could not create ReservationService object" + e.getMessage());
        }
    }


    /**
     * loadData:
     * Given a filepath, will load all relevant data into reserveList,
     * and reservationMap. Calls the ReservationLoader.loadReservations
     * method to achieve this. Also will fill all validIDNums for use
     * in creating and deleting future reservations.
     * @param filePath
     */
    /*public void loadData(Path filePath) {
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

    } */

    /**
     * TODO: IF there are more than 999 reservations, how do we handle that edge case?
     * TODO: Account for exception throwing
     * TODO: Create test cases for if a new reservation is invalid, what does that mean for validIdNums
     * createReservation: Will create a new reservation and place it in both reserveList
     * and reservationMap.
     * @param roomList
     * @param start
     * @param end
     * @param bill
     */
    public void createReservation(List<Room> roomList , LocalDate start, LocalDate end, double bill)  {
        //Creates a new reservation ID from the set of all valid reservation ids
        String newResId = createResId();
        //Confirms that the two given dates are within a valid range
        if (!DateRange.validateRange(start, end)) {
            JOptionPane.showMessageDialog(null, "Invalid date range");
            return;
        }
        //TODO: Delete
        //Reservation newRes = new Reservation(newResId, roomList, new DateRange(start, end), bill);
        //reserveList.add(newRes);
        String createReservation = """
                INSERT INTO Reservations (resId, startDate, endDate, bill, checkedIn) VALUES (?,?,?,?,?);
                """;

        String createReservedRooms = """
                INSERT INTO ReservedRooms (resId, roomNo, floorNum) VALUES (?,?,?);
                """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement reservePstmt = conn.prepareStatement(createReservation);
             PreparedStatement reservedRoomsPstmt = conn.prepareStatement(createReservedRooms)) {
            conn.setAutoCommit(false);

            reservePstmt.setString(1, newResId);
            reservePstmt.setString(2, String.valueOf(start));
            reservePstmt.setString(3, String.valueOf(end));
            reservePstmt.setDouble(4, bill);
            //TODO: Ensure works
            reservePstmt.setBoolean(5, false);
            reservePstmt.executeUpdate();
            conn.commit();

            for (Room r : roomList) {
                reservedRoomsPstmt.setString(1, newResId);
                reservedRoomsPstmt.setInt(2, r.getRoomNo());
                reservedRoomsPstmt.setInt(3, r.getFloorNum());

                reservedRoomsPstmt.addBatch();
            }
            reservedRoomsPstmt.executeBatch();
            conn.commit();
            System.out.println("Reservation created successfully: " + newResId);

        }catch (SQLException e){
            System.err.println("Error creating reservation: " + e.getMessage());
        }

        /*try {
            assert newResId != null;
            //reservationMap.put(newResId.toUpperCase(), newRes);
        }*/
        catch (NullPointerException e) {
            System.err.println("Can not assign new reservation ID: " + e.getMessage());
        }
        //JOptionPane.showMessageDialog(null, "Go to Booking / Confirmation page");
    }

    //TODO: Must be able to write to the database/file and remove/add reservations
    //TODO: Possibly remove try/catch statement
    public void deleteReservation(String reservationId) {
        String deleteReservation = """
                DELETE FROM Reservations WHERE resId = ?;
                """;
        String deleteReservedRooms = """
                DELETE FROM ReservedRooms WHERE resId = ?;
                """;

        try (Connection conn = DBUtil.getConnection();
        PreparedStatement dReserve = conn.prepareStatement(deleteReservation);
        PreparedStatement dReservedRooms = conn.prepareStatement(deleteReservedRooms)) {
            int resOrigSize, resRoomOrigSize, resNewSize, resRoomNewSize;

            resOrigSize = getCountOfAllInTable("Reservations");
            resRoomOrigSize = getCountOfAllInTable("ReservedRooms");

            conn.setAutoCommit(false);

            dReserve.setString(1, reservationId);
            dReservedRooms.setString(1,reservationId);
            dReservedRooms.executeUpdate();
            dReserve.executeUpdate();
            conn.commit();

            resNewSize = getCountOfAllInTable("Reservations");
            resRoomNewSize = getCountOfAllInTable("ReservedRooms");
            if (resNewSize >= resOrigSize && resRoomNewSize >= resRoomOrigSize) {
                System.out.println("Reservation not found: " + reservationId);
            }
            else {
                System.out.println("Reservation found and deleted: " + reservationId);
                ValidIDNums.remove(Integer.parseInt(reservationId.substring(2)));
            }

        } catch (SQLException e) {
            System.err.println("Error deleting reservation: " + e.getMessage());
        }


        /*try {
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
        } */
    }

    //TODO: Implement
    public void removeRoomFromReservation(String reservationId, int roomNumber) {
        String deleteReservedRoom = """
                DELETE FROM Reservations WHERE resId = ? AND roomNo = ?;
        """;
    }

    /**
     * Given the desired table name, this function will return the number
     * of rows in the selected table.
     *
     * @param tableName
     * @return
     */
    public int getCountOfAllInTable(String tableName) {
        String getCount = """
                SELECT COUNT (*) FROM
                """ +  tableName;
        try (Connection conn = DBUtil.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(getCount);
            return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error getting count of table: " + tableName + e.getMessage());
        }
        return 0;
    }

    /**
     * TODO: Must test if there are over 999 reservations
     * @return The new reservationID with the smallest value of the
     * reservation. If there are no valid numbers left, the function will
     * return null. The bounds of the valid numbers are [1, 999].
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
    private void fillValidIDNums() throws SQLException {
        ValidIDNums.clear();
        try (Connection conn = DBUtil.getConnection();) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT resId FROM Reservations");
            while (rs.next()) {
                String id = rs.getString("resId");
                int num = Integer.parseInt(id.substring(2)); // "R-001" → 1
                ValidIDNums.add(num);
            }
        } catch (SQLException e) {
            System.err.println("Error filling valid id numbers: " + e.getMessage());
            throw e;
        }

        /*for (int i = 0; i < reserveList.size(); i++) {
            Integer num = Integer.parseInt(reserveList.get(i).getId().substring(2));
            ValidIDNums.add(num);
        } */
    }


    //TODO: Must implement (Only need to get the date range and the checked in status)
    public boolean hasValidReservation(int guestID) {
        //try (Connection conn = DBUtil.getConnection();)
        //

        return false;
    }

    /**
     * findReservation: Searches for a reservation via it's ID
     * @param reservationId ID of the sought after reservation
     * @return the Reservation object if it exists, null if otherwise
     */
    public Reservation findReservation(String reservationId) throws SQLException {
        String findRes = """
                SELECT * FROM Reservations WHERE resId = ?;
                """;
        String findResRoom = """
                SELECT * FROM ReservedRooms WHERE resId = ?;
                """;
        try (Connection conn = DBUtil.getConnection();
            PreparedStatement resPstmt = conn.prepareStatement(findRes);
            PreparedStatement resRoomPstmt = conn.prepareStatement(findResRoom)) {
            conn.setAutoCommit(false);
            resPstmt.setString(1, reservationId);
            resRoomPstmt.setString(1, reservationId);


            ResultSet resRS = resPstmt.executeQuery();
            ResultSet resRoomRS = resRoomPstmt.executeQuery();

            //If there exists a reservation object in both tables
            if (resRS.isBeforeFirst() && resRoomRS.isBeforeFirst()) {
                Reservation userReservation = buildReservationFromResultSet(resRS, resRoomRS);
                return userReservation;
                // throw new NullPointerException("Reservation not found");
            }
            //If there doesn't exist a reservation object, then will return null
            System.out.println("Reservation not found: " + reservationId);
            return null;


        } catch (SQLException e) {
            System.err.println("Error finding reservation: " + e.getMessage());
            throw e;
        }
        /*
        if (reservationMap.containsKey(reservationId)) {
            return reservationMap.get(reservationId.toUpperCase());
        } */
        //return null;
    }


    /**
     * Given two result sets for both the Reservations and ReservedRooms
     * databases, this function will use them both to create a reservation
     * object. This function also uses RoomService's findRoom() function to
     * create and fill the list with rooms necessary for the roomList (List<Room>)
     * of the reservation class
     *
     * @param resRS
     * @param resRoomRS
     * @return
     * @throws SQLException
     */
    private Reservation buildReservationFromResultSet(ResultSet resRS, ResultSet resRoomRS) throws SQLException {
        String resId = resRS.getString("resId");
        LocalDate startDate = LocalDate.parse(resRS.getString("startDate"));
        LocalDate endDate = LocalDate.parse(resRS.getString("endDate"));
        double bill = resRS.getDouble("bill");
        boolean checkedIn = resRS.getBoolean("checkedIn");

        List<Room> roomList = new ArrayList<>();

        try {
            //Creating a room service object for access to findRoom()
            RoomService roomService = new RoomService();
            while (resRoomRS.next()) {
                int roomNo = resRoomRS.getInt("roomNo");
                roomList.add(roomService.findRoom(roomNo));
            }
        } catch (SQLException e) {
            System.err.println("Error building reservation: " + e.getMessage());
            throw e;
        }
        return new Reservation(resId, roomList, new DateRange(startDate, endDate), bill, checkedIn);
    }

    //Find all rooms in the list that overlap with the given dateRange
    //In SearchController, find the intersection of those rooms which do not overlap
    public List<Room> findOverlaps(List<Room> currentAvailableRooms, DateRange possibleOverlap) {
        //Set<Room> overlaps = new HashSet<>();
        return currentAvailableRooms.stream().filter(r ->  r.isAvailable(possibleOverlap, reserveList))
                .toList();
    }

    public List<Reservation> getReservations() {
        return reserveList;
    }

    public Map<String, Reservation> getReservationMap() {
        return reservationMap;
    }

    /**
     * getReservationsForRoom: Will return a list of reservations that a room has
     * @param room
     * @return
     */
    public List<Reservation> getReservationsForRoom(Room room) {
        return reserveList.stream().filter(r -> r.getRooms().contains(room))
                .toList();
    }

    // this is a stub for now, im not sure how we are going to reservations to a guestID,
    // so validation by guest cannot be implemented safely yet
    public boolean hasValidReservation(int guestID) {
        return true;
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
