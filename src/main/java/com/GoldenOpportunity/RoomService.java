package com.GoldenOpportunity;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.*;

public class RoomService {
    public RoomService() {
        // No roomList, no roomMap — DB only
    }

    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS rooms (
                    floorNum   INTEGER NOT NULL,
                    roomNo     INTEGER PRIMARY KEY,
                    numBeds    INTEGER NOT NULL,
                    smoking    BOOLEAN NOT NULL,
                    qLevel     TEXT NOT NULL,
                    roomType   TEXT NOT NULL,
                    rate       REAL NOT NULL,
                    bedTypes   TEXT NOT NULL,
                    capacity    INTEGER NOT NULL,
                    description TEXT,
                    image       TEXT
                );
                """;

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create table.", e);
        }
    }

    public void deleteRoomTable(){
        String sql = "DROP TABLE IF EXISTS rooms;";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete table.", e);
        }
    }

    /** Load ALL rooms from DB */
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();

        String sql = "SELECT floorNum, roomNo, numBeds, smoking, qLevel," +
                " roomType, rate, bedTypes, capacity, description, image FROM rooms";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                rooms.add(buildRoomFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rooms;
    }

    /** Find a single room by room number */
    public Room findRoom(int roomNo) {
        String sql = "SELECT * FROM rooms WHERE roomNo = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomNo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return buildRoomFromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /** Search rooms using Criteria */
    public List<Room> searchRoom(Criteria c) {
        List<Room> results = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM rooms WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (c.getFloorNum() != 0) {
            sql.append(" AND floorNum = ?");
            params.add(c.getFloorNum());
        }
        if (c.getRoomNum() != 0) {
            sql.append(" AND roomNo = ?");
            params.add(c.getRoomNum());
        }
        if (c.getRoomType() != null && !c.getRoomType().isBlank()) {
            sql.append(" AND LOWER(roomType) = LOWER(?)");
            params.add(c.getRoomType());
        }
        if (c.getQuality() != null && !c.getQuality().isBlank()) {
            sql.append(" AND LOWER(qLevel) = LOWER(?)");
            params.add(c.getQuality());
        }
        if (c.isSmoking()) {
            sql.append(" AND smoking = 1");
        }
        if (c.getNumBeds() > 0) {
            sql.append(" AND numBeds = ?");
            params.add(c.getNumBeds());
        }
        if (c.getRate() > 0) {
            sql.append(" AND rate <= ?");
            params.add(c.getRate());
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Room room = buildRoomFromResultSet(rs);

                // Bed type filtering must be done in Java
                if (!c.getBeds().isEmpty()) {
                    if (!bedTypesMatch(c.getBeds(), room.getBedTypes())) {
                        continue;
                    }
                }

                results.add(room);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    public void loadRoomsFromCSV(String filename) throws FileNotFoundException {
        File file = new File(filename);
        Scanner fileScanner = new Scanner(file);

        // Skip header
        if (fileScanner.hasNextLine()) {
            fileScanner.nextLine();
        }

        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            String[] parts = parseCSVLine(line);

            int floorNum = Integer.parseInt(parts[0]);
            int roomNo = Integer.parseInt(parts[1]);
            String roomType = parts[2];
            String qLevel = parts[3];
            int numBeds = Integer.parseInt(parts[4]);
            Map<String, Integer> bedTypes = parseBedTypes(parts[5]);
            boolean smoking = Boolean.parseBoolean(parts[6]);
            double rate = Double.parseDouble(parts[7]);
            int capacity = numBeds * 2;
            String description = String.valueOf(roomNo) + " description";
            String image = "insert image file path here";

            createRoom(floorNum, roomNo, numBeds, smoking, qLevel, roomType,
                    rate, bedTypes, capacity, description, image);
        }

        fileScanner.close();
    }

    private String[] parseCSVLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim());

        return tokens.toArray(new String[0]);
    }

    /** Create a new room in DB */
    public void createRoom(int floorNum, int rmNo, int numBeds, boolean smoke,
                           String qlty, String rmType, double rate,
                           Map<String, Integer> bedTypesInput, int capacity,
                           String description, String image) {

        String sql = "INSERT INTO rooms (floorNum, roomNo, numBeds, smoking, qLevel, roomType, rate, bedTypes, " +
                "capacity, description, image) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, floorNum);
            stmt.setInt(2, rmNo);
            stmt.setInt(3, numBeds);
            stmt.setBoolean(4, smoke);
            stmt.setString(5, qlty);
            stmt.setString(6, rmType);
            stmt.setDouble(7, rate);
            stmt.setString(8, serializeBedTypes(bedTypesInput));
            stmt.setInt(9, capacity);
            stmt.setString(10, description);
            stmt.setString(11, image);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Modify an existing room in DB */
    public boolean modifyRoom(Criteria criteria) {


        int MfloorNum = criteria.getFloorNum();
        int MrmNo = criteria.getRoomNum();
        int MnumBeds = criteria.getNumBeds();
        boolean Msmoke = criteria.isSmoking();
        String Mqlty = criteria.getQuality();
        String MrmType = criteria.getRoomType();
        double Mrate = criteria.getRate();
        Map<String, Integer> MbedTypesInput = criteria.getBeds();
        int Mcapacity = criteria.getCapacity();
        String Mdescription = criteria.getDescription();
        String Mimage = criteria.getImage();
        // --- VALIDATION SECTION ---

        Room room = this.findRoom(MrmNo);
        // Floor must be 1–3
        if (MfloorNum < 1 || MfloorNum > 3) {
            MfloorNum = room.getFloorNum();
        }

        // Room number must start with floor number (e.g., 101, 203, 315)
        int firstDigit = Integer.parseInt(Integer.toString(MrmNo).substring(0, 1));
        if (firstDigit != MfloorNum) {
            MfloorNum = room.getFloorNum();
        }

        // Beds must be positive
        if (MnumBeds <= 0) {
            MnumBeds = room.getBeds();
        }

        // Rate must be positive
        if (Mrate <= 0) {
            Mrate = room.getRate();
        }

        // Quality and room type must not be blank
        if (Mqlty == null || Mqlty.isBlank()) {
            Mqlty = room.getQLevel();
        }
        if (MrmType == null || MrmType.isBlank()) {
            MrmType = room.getRoomType();
        }

        // Bed types must not be empty and must match numBeds
        if (MbedTypesInput == null || MbedTypesInput.isEmpty()) {
            MbedTypesInput = room.getBedTypes();
        }

        int bedTypeTotal = MbedTypesInput.values().stream().mapToInt(i -> i).sum();
        if (bedTypeTotal != MnumBeds) {
            MnumBeds = room.getBeds();
        }

        // Capacity must be >= number of beds
        if (Mcapacity < MnumBeds) {
            Mcapacity = room.getCapacity();
        }

        //Validate description
        if(Mdescription.isBlank()){
            Mdescription = room.getDescription();
        }

        // Validate image path exists
        if(Mimage.isBlank()){
            Mimage = room.getImage();
        }
        if (Mimage != null && !Mimage.isBlank()) {
            File f = new File(Mimage);
            if (!f.exists()) {
                System.out.println("Warning: Image file does not exist.");
                return false;
            }
        }

        String sql = "UPDATE rooms SET floorNum=?, numBeds=?, smoking=?, qLevel=?, roomType=?," +
                " rate=?, bedTypes=?, capacity=?, description=?, image=? " +
                "WHERE roomNo=?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, MfloorNum);
            stmt.setInt(2, MnumBeds);
            stmt.setBoolean(3, Msmoke);
            stmt.setString(4, Mqlty);
            stmt.setString(5, MrmType);
            stmt.setDouble(6, Mrate);
            stmt.setString(7, serializeBedTypes(MbedTypesInput));
            stmt.setInt(8, Mcapacity);
            stmt.setString(9, Mdescription);
            stmt.setString(10, Mimage);
            stmt.setInt(11, MrmNo);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    // -------------------------
    // Helper Methods
    // -------------------------

    private Room buildRoomFromResultSet(ResultSet rs) throws SQLException {
        int floorNum = rs.getInt("floorNum");
        int roomNo = rs.getInt("roomNo");
        int numBeds = rs.getInt("numBeds");
        boolean smoking = rs.getBoolean("smoking");
        String qLevel = rs.getString("qLevel");
        String roomType = rs.getString("roomType");
        double rate = rs.getDouble("rate");
        int capacity = rs.getInt("capacity");
        String description = rs.getString("description");
        String image = rs.getString("image");

        Map<String, Integer> bedTypes = parseBedTypes(rs.getString("bedTypes"));

        return new Room(floorNum, roomNo, numBeds, smoking, qLevel, roomType,
                rate, bedTypes, capacity, description, image);
    }

    private Map<String, Integer> parseBedTypes(String input) {
        Map<String, Integer> map = new HashMap<>();
        if (input == null || input.isBlank()) return map;

        String[] parts = input.split(",");
        for (String part : parts) {
            part = part.trim();
            String[] tokens = part.split(" ");
            int count = Integer.parseInt(tokens[0]);
            String type = tokens[1];
            map.put(type, count);
        }
        return map;
    }

    private String serializeBedTypes(Map<String, Integer> map) {
        StringBuilder sb = new StringBuilder();
        for (var e : map.entrySet()) {
            sb.append(e.getValue()).append(" ").append(e.getKey()).append(", ");
        }
        return sb.toString().replaceAll(", $", "");
    }

    private boolean bedTypesMatch(Map<String, Integer> required, Map<String, Integer> available) {
        for (var e : required.entrySet()) {
            if (available.getOrDefault(e.getKey(), 0) < e.getValue()) {
                return false;
            }
        }
        return true;
    }
}