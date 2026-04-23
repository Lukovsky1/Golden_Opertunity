package com.GoldenOpportunity;

import com.GoldenOpportunity.DatabaseTools.DBUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReservationLoader extends Loader{

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("M/d/yy");

    /**
     * loadReservations: Used to load all reservation objects from the data files.
     * @param csvPath
     * @return
     * @throws IOException
     */
    public List<Reservation> loadReservations(Path csvPath) throws IOException {
        List<String> lines = Files.readAllLines(csvPath);
        List<Reservation> reservations = new ArrayList<>();
        RoomService roomService = new RoomService();

        // Skip header
        for (int i = 1; i < lines.size(); i++) {
            List<Room> roomsList = new ArrayList<>();
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(",");

            String ID = parts[0].trim();
            int roomNumber = Integer.parseInt(parts[1].trim());
            //TODO: Make sure this works to get all room data into the List
            //roomsList.add(RoomService.roomMap.get(roomNumber));
            //Room reservedRoom = roomService.findRoom(roomNumber);
            LocalDate start = LocalDate.parse(parts[2].trim(), FORMATTER);
            LocalDate end = LocalDate.parse(parts[3].trim(), FORMATTER);

            DateRange range = new DateRange(start, end);


            // Bill is unknown → set to 0 for now
            reservations.add(new Reservation(ID, roomsList, range, 0.0));
        }

        return reservations;
    }

    @Override
    public void createTable() {
        //FIXME: Primary key should be roomNo ALONE. Because floorNum is non-unique,
        //FIXME: the statement will be made invalid
        String createReservation = """
                CREATE TABLE IF NOT EXISTS Reservations (
                      resId TEXT PRIMARY KEY,
                      startDate TEXT NOT NULL,
                      endDate TEXT NOT NULL,
                      bill REAL NOT NULL
                  );

                """;

        String createReservedRooms = """
                CREATE TABLE IF NOT EXISTS ReservedRooms (
                      resId TEXT NOT NULL,
                      roomNo INTEGER NOT NULL,
                      floorNum INTEGER NOT NULL,
                      PRIMARY KEY (resId, roomNo, floorNum),
                      FOREIGN KEY (resId) REFERENCES Reservation(resId),
                      FOREIGN KEY (roomNo, floorNum) REFERENCES Rooms(roomNo, floorNum)
                );
                """;

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createReservation);
            stmt.execute(createReservedRooms);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create table.", e);
        }
    }

    @Override
    public void loadData() {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            String reserveSql = Files.readString(Path.of("src/main/resources/reservation_insert.sql"));
            String reservedRoomsSql = Files.readString(Path.of("src/main/resources/reservedRooms_insert.sql"));
            stmt.executeUpdate(reserveSql);
            stmt.executeUpdate(reservedRoomsSql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load SQL data.", e);
        } catch (IOException e) {
            System.err.println(e.getMessage() + e.getCause());
        }
    }
}