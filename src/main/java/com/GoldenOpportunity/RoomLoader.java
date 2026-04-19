package com.GoldenOpportunity;

import javax.swing.text.html.HTMLDocument;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class RoomLoader extends Loader {
    @Override
    public void createTable() {
        //FIXME: Primary key should be roomNo ALONE. Because floorNum is non-unique,
        //FIXME: the statement will be made invalid
        String createRoom = """
                CREATE TABLE IF NOT EXISTS Room (
                  roomNo INTEGER PRIMARY KEY NOT NULL,
                  floorNum INTEGER NOT NULL,
                  beds INTEGER NOT NULL,
                  smoking BOOLEAN NOT NULL,
                  qLevel TEXT NOT NULL,
                  roomType TEXT NOT NULL,
                  rate REAL NOT NULL
                );
                """;

        String Beds = """
                CREATE TABLE IF NOT EXISTS Beds (
                    roomNo INTEGER NOT NULL REFERENCES Room(roomNo),
                    floorNum INTEGER NOT NULL REFERENCES Room(floorNum),
                    bedType TEXT NOT NULL,
                    quantity INTEGER NOT NULL
                );
                """;

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createRoom);
            stmt.execute(Beds);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create table.", e);
        }
    }
    //TODO: Might need to be edited to loadData(String filename) for better ease
    //TODO: of editing in case the file location changes
    @Override
    public void loadData() {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            String roomSql = Files.readString(Path.of("src/main/resources/room_insert.sql"));
            String bedSql = Files.readString(Path.of("src/main/resources/bed_insert.sql"));
            stmt.executeUpdate(roomSql);
            stmt.executeUpdate(bedSql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load SQL data.", e);
        } catch (IOException e) {
            System.err.println(e.getMessage() + e.getCause());
        }
    }

    /*
    int floorNum;
    int roomNo;
    int beds;
    Map<String, Integer> bedTypes;
    boolean smoking;
    String qLevel;
    String roomType;
    double rate;
     */

    /*
    CREATE TABLE IF NOT EXISTS Employee (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE,
                    age INTEGER NOT NULL,
                    gender TEXT NOT NULL,
                    salary INTEGER NOT NULL,
                    department TEXT NOT NULL
                );
     */
}

