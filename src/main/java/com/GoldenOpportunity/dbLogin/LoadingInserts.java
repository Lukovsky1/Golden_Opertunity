package com.GoldenOpportunity.dbLogin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


//FIXME: Depreciated
public class LoadingInserts {
    private static final String roomFile = "src/main/resources/room_insertWBedTypes.sql";
    private static final String reservationFile = "src/main/resources/reservation_insert.sql";
    private static final String reservedRoomsFile = "src/main/resources/reservedRooms_insert.sql";

    public static void createRooms(Connection con) throws SQLException, IOException {
        con.setAutoCommit(false);
        Statement st = con.createStatement();
        try {
            st.executeUpdate(Files.readString(Path.of(roomFile)));
            con.commit();
        } catch (SQLException ex) {
            System.err.println("Error loading rooms");
            throw ex;
        } catch (IOException ex) {
            System.err.println("Error reading room insert file");
            throw ex;
        }
        /**
         * Files.readString(Path.of("src/main/resources/reservation_insert.sql"));
         *             String reservedRoomsSql = Files.readString(Path.of("src/main/resources/reservedRooms_insert.sql"));
         *             stmt.executeUpdate(reserveSql);
         *             stmt.executeUpdate(reservedRoomsSql);
         */
    }
    public static void createReservations(Connection con) throws SQLException,  IOException {
        con.setAutoCommit(false);
        Statement st = con.createStatement();
        try {
            st.executeUpdate(Files.readString(Path.of(reservationFile)));
            con.commit();
        } catch (SQLException ex) {
            System.err.println("Error loading reservation insert file");
            throw ex;
        } catch (IOException ex) {
            System.err.println("Error reading reservation insert file");
            throw ex;
        }
    }
    public static void createReservedRooms(Connection con) throws SQLException, IOException {
        con.setAutoCommit(false);
        Statement st = con.createStatement();
        try {
            st.executeUpdate(Files.readString(Path.of(reservedRoomsFile)));
            con.commit();
        } catch (SQLException ex) {
            System.err.println("Error loading reservedRooms insert file");
            throw ex;
        } catch (IOException ex) {
            System.err.println("Error reading reservedRooms insert file");
            throw ex;
        }
    }
    public LoadingInserts() {}
}
