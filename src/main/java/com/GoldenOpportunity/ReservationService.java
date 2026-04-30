package com.GoldenOpportunity;

import com.GoldenOpportunity.DatabaseTools.DBUtil;
import com.GoldenOpportunity.dbLogin.GuestReservationDao;

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
    //private List<Reservation> reserveList = new ArrayList<>();
    //private ReservationLoader reservationLoader =  new ReservationLoader();
    //ReservationId mapped to its reservation object
    //private Map<String, Reservation> reservationMap = new HashMap<>();
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
    public String createReservation(List<Room> roomList , LocalDate start, LocalDate end, double bill, String name)  throws SQLException {
        //Creates a new reservation ID from the set of all valid reservation ids
        String newResId = createResId();
        if (newResId == null) {
            System.err.println("No available reservation IDs");
            return null;
        }
        //Confirms that the two given dates are within a valid range
        if (!DateRange.validateRange(start, end)) {
            JOptionPane.showMessageDialog(null, "Invalid date range");
            return null;
        }
        //TODO: Delete
        //Reservation newRes = new Reservation(newResId, roomList, new DateRange(start, end), bill);
        //reserveList.add(newRes);
        String createReservation = """
                INSERT INTO Reservations (resId, startDate, endDate, bill, checkedIn, name) VALUES (?,?,?,?,?,?);
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
            reservePstmt.setString(6,name);
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
            return newResId;

        }catch (SQLException e){
            System.err.println("Error creating reservation: " + e.getMessage());
            try  {
                DBUtil.getConnection().rollback();
            } catch (SQLException ignored) {}
            throw e;
        }
        //JOptionPane.showMessageDialog(null, "Go to Booking / Confirmation page");
    }

    //TODO: Must be able to write to the database/file and remove/add reservations
    //TODO: Possibly remove try/catch statement
    public void deleteReservation(String reservationId) throws SQLException {
        String deleteReservation = """
                DELETE FROM Reservations WHERE resId = ?;
                """;
        String deleteReservedRooms = """
                DELETE FROM ReservedRooms WHERE resId = ?;
                """;

        try (Connection conn = DBUtil.getConnection();
        PreparedStatement dReserve = conn.prepareStatement(deleteReservation);
        PreparedStatement dReservedRooms = conn.prepareStatement(deleteReservedRooms)) {

            conn.setAutoCommit(false);

            dReserve.setString(1, reservationId);
            dReservedRooms.setString(1,reservationId);

            int roomsDeleted = dReservedRooms.executeUpdate();
            int resDeleted = dReserve.executeUpdate();

            if (roomsDeleted == 0 && resDeleted == 0) {
                System.out.println("Reservation not found: " + reservationId);
                return;
            }

            conn.commit();

            System.out.println("Reservation found and deleted: " + reservationId);
            ValidIDNums.remove(Integer.parseInt(reservationId.substring(2)));


        } catch (SQLException e) {
            System.err.println("Error deleting reservation: " + e.getMessage());
            try {
                DBUtil.getConnection().rollback();
            } catch (SQLException ignored) {}
            throw e;
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

        String sql = "SELECT resId FROM Reservations";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {

            conn.setAutoCommit(false);

            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String id = rs.getString("resId");
                int num = Integer.parseInt(id.substring(2));
                ValidIDNums.add(num);
            }

            conn.commit();

        } catch (SQLException e) {
            System.err.println("Error filling valid ID numbers: " + e.getMessage());
            try (Connection conn = DBUtil.getConnection()) {
                conn.rollback();
            } catch (SQLException ignored) {}
            throw e;
        }
    }



    //TODO: Must implement (Only need to get the date range and the checked in status)
    public boolean hasValidReservation(int guestID) throws SQLException {
        String checked = "SELECT checkedIn, startDate, endDate FROM Reservations WHERE resId = ?;";

        GuestReservationDao guestReservationDao = new GuestReservationDao();
        List<String> resIds = guestReservationDao.findGuestReservations(guestID);

        boolean validRes = false;

        for (String resId : resIds) {
            try (Connection conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(checked);) {
                pstmt.setString(1, resId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    validRes = rs.getBoolean("checkedIn");
                    LocalDate startDate = rs.getDate("startDate").toLocalDate();
                    LocalDate endDate = rs.getDate("endDate").toLocalDate();

                    if (validRes && (startDate.isAfter(LocalDate.now()) && endDate.isBefore(LocalDate.now()))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Reservation modifyReservation(String reservationId, LocalDate newStartDate, LocalDate newEndDate, String name) throws SQLException {
        Reservation existingReservation = findReservation(reservationId);
        if (existingReservation == null) {
            throw new IllegalArgumentException("Reservation not found.");
        }
        return modifyReservation(reservationId, newStartDate, newEndDate, existingReservation.getRooms(), name);
    }

    public Reservation modifyReservation(String reservationId, LocalDate newStartDate, LocalDate newEndDate,
                                         List<Room> newRooms, String name) throws SQLException {
        if (reservationId == null || reservationId.isBlank()) {
            throw new IllegalArgumentException("Reservation ID is required.");
        }
        if (newStartDate == null || newEndDate == null) {
            throw new IllegalArgumentException("Both start and end dates are required.");
        }
        if (!newStartDate.isBefore(newEndDate)) {
            throw new IllegalArgumentException("End date must be after start date.");
        }
        if (newRooms == null || newRooms.isEmpty()) {
            throw new IllegalArgumentException("At least one room is required.");
        }

        Reservation existingReservation = findReservation(reservationId);
        if (existingReservation == null) {
            throw new IllegalArgumentException("Reservation not found.");
        }
        if (existingReservation.isCheckedIn()) {
            throw new IllegalArgumentException("Checked-in reservations cannot be modified.");
        }
        if (!roomsAreAvailableForUpdate(existingReservation.getId(), newRooms, new DateRange(newStartDate, newEndDate))) {
            throw new IllegalArgumentException("Selected dates are not available for this reservation.");
        }

        long nights = java.time.temporal.ChronoUnit.DAYS.between(newStartDate, newEndDate);
        double newBill = newRooms.stream()
                .mapToDouble(Room::getRate)
                .sum() * nights;

        String updateReservationSql = """
                UPDATE Reservations
                SET startDate = ?, endDate = ?, bill = ?, name = ?
                WHERE resId = ?
                """;
        String deleteReservedRoomsSql = "DELETE FROM ReservedRooms WHERE resId = ?";
        String insertReservedRoomSql = "INSERT INTO ReservedRooms (resId, roomNo, floorNum) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement updateReservationPs = conn.prepareStatement(updateReservationSql);
             PreparedStatement deleteReservedRoomsPs = conn.prepareStatement(deleteReservedRoomsSql);
             PreparedStatement insertReservedRoomPs = conn.prepareStatement(insertReservedRoomSql)) {
            conn.setAutoCommit(false);

            updateReservationPs.setString(1, newStartDate.toString());
            updateReservationPs.setString(2, newEndDate.toString());
            updateReservationPs.setDouble(3, newBill);
            updateReservationPs.setString(4, name);
            updateReservationPs.setString(5, reservationId);
            updateReservationPs.executeUpdate();

            deleteReservedRoomsPs.setString(1, reservationId);
            deleteReservedRoomsPs.executeUpdate();

            for (Room room : newRooms) {
                insertReservedRoomPs.setString(1, reservationId);
                insertReservedRoomPs.setInt(2, room.getRoomNo());
                insertReservedRoomPs.setInt(3, room.getFloorNum());
                insertReservedRoomPs.addBatch();
            }
            insertReservedRoomPs.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            try (Connection conn = DBUtil.getConnection()) {
                conn.rollback();
            } catch (SQLException ignored) {}
            throw e;
        }

        return findReservation(reservationId);
    }

    private boolean roomsAreAvailableForUpdate(String reservationIdToModify, List<Room> requestedRooms,
                                               DateRange requestedRange) throws SQLException {
        for (Room room : requestedRooms) {
            List<Reservation> roomReservations = getReservationsForRoom(room);
            for (Reservation roomReservation : roomReservations) {
                if (roomReservation.getId().equalsIgnoreCase(reservationIdToModify)) {
                    continue;
                }
                if (roomReservation.getDateRange().overlaps(requestedRange)) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<Room> parseRooms(String rawRooms) throws SQLException {
        if (rawRooms == null || rawRooms.isBlank()) {
            throw new IllegalArgumentException("At least one room is required.");
        }

        RoomService roomService = new RoomService();
        List<Room> rooms = new ArrayList<>();
        Set<Integer> seenRoomNumbers = new HashSet<>();

        for (String token : rawRooms.split(",")) {
            String trimmedToken = token.trim();
            if (trimmedToken.isEmpty()) {
                continue;
            }

            int roomNumber;
            try {
                roomNumber = Integer.parseInt(trimmedToken);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Room numbers must be comma-separated integers.");
            }

            if (!seenRoomNumbers.add(roomNumber)) {
                continue;
            }

            Room room = roomService.findRoom(roomNumber);
            if (room == null) {
                throw new IllegalArgumentException("Room " + roomNumber + " does not exist.");
            }
            rooms.add(room);
        }

        if (rooms.isEmpty()) {
            throw new IllegalArgumentException("At least one room is required.");
        }

        return rooms;
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
            DBUtil.getConnection().rollback();
            throw e;
        }
    }
    //TODO: implement
    public Reservation findReservationName(String name) throws SQLException {
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
            DBUtil.getConnection().rollback();
            throw e;
        }
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
        String name = resRS.getString("name");

        List<Room> roomList = new ArrayList<>();

        try {
            //Creating a room service object for access to findRoom()
            RoomService roomService = new RoomService();
            while (resRoomRS.next()) {
                int roomNo = resRoomRS.getInt("roomNo");
                Room room = roomService.findRoom(roomNo);
                if (room != null) {
                    roomList.add(room);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error building reservation: " + e.getMessage());
            throw e;
        }
        return new Reservation(resId, roomList, new DateRange(startDate, endDate), bill, checkedIn, name);
    }

    //FIXME: Might be depreciated
    //Find all rooms in the list that overlap with the given dateRange
    //In SearchController, find the intersection of those rooms which do not overlap
    public List<Room> findOverlaps(List<Room> currentAvailableRooms, DateRange possibleOverlap) {
        //Set<Room> overlaps = new HashSet<>();
        return currentAvailableRooms.stream().filter(r -> {
                    try {
                        return r.isAvailable(possibleOverlap, getAllReservations());
                    } catch (SQLException e) {
                        System.err.println("Error getting overlaps: " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }

    public List<Reservation> getAllReservations() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();

        String sqlReservations = "SELECT * FROM Reservations";
        String sqlRooms = "SELECT * FROM ReservedRooms WHERE resId = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement resStmt = conn.prepareStatement(sqlReservations);
             PreparedStatement roomsStmt = conn.prepareStatement(sqlRooms)) {

            conn.setAutoCommit(false);

            ResultSet resRS = resStmt.executeQuery();

            while (resRS.next()) {
                String resId = resRS.getString("resId");

                // Load all rooms for this reservation
                roomsStmt.setString(1, resId);
                ResultSet resRoomRS = roomsStmt.executeQuery();

                // Build the reservation using your helper
                Reservation reservation = buildReservationFromResultSet(resRS, resRoomRS);
                reservations.add(reservation);
            }

            conn.commit();
            return reservations;

        } catch (SQLException e) {
            System.err.println("Error getting all reservations: " + e.getMessage());
            try (Connection conn = DBUtil.getConnection()) {
                conn.rollback();
            } catch (SQLException ignored) {}
            throw e;
        }
    }

    /**
     * getReservationsForRoom: Will return a list of reservations that a room has
     * TODO: Must be tested
     * @param room
     * @return
     */
    public List<Reservation> getReservationsForRoom(Room room) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();

        String sqlRes = """
        SELECT * FROM Reservations
        WHERE resId IN (
            SELECT resId FROM ReservedRooms
            WHERE roomNo = ? AND floorNum = ?
        );
    """;

        String sqlRooms = "SELECT * FROM ReservedRooms WHERE resId = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement resStmt = conn.prepareStatement(sqlRes);
             PreparedStatement roomsStmt = conn.prepareStatement(sqlRooms)) {

            conn.setAutoCommit(false);

            resStmt.setInt(1, room.getRoomNo());
            resStmt.setInt(2, room.getFloorNum());

            ResultSet resRS = resStmt.executeQuery();

            while (resRS.next()) {
                String resId = resRS.getString("resId");

                roomsStmt.setString(1, resId);
                ResultSet resRoomRS = roomsStmt.executeQuery();

                reservations.add(buildReservationFromResultSet(resRS, resRoomRS));
            }

            conn.commit();
            return reservations;

        } catch (SQLException e) {
            System.err.println("Error getting reservations for room: " + e.getMessage());
            try (Connection conn = DBUtil.getConnection()) {
                conn.rollback();
            } catch (SQLException ignored) {}
            throw e;
        }
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
