import com.GoldenOpportunity.DatabaseTools.DBInitializer;
import com.GoldenOpportunity.DatabaseTools.DBUtil;
import com.GoldenOpportunity.Reservation;
import com.GoldenOpportunity.ReservationService;
import com.GoldenOpportunity.Room;
import com.GoldenOpportunity.RoomService;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationTests {
    @BeforeAll
    public static void setup() throws SQLException {
        DBUtil.setTestDbDirectory("src/test/resources");
        //DBUtil.deleteDatabase("src/test/resources/golden.db");
        try {
            DBInitializer.initialize();
            System.out.println("Database created");
        } catch (SQLException e ) {
            System.err.println("Error initializing database");
            System.err.println(e.getMessage());
            fail();
        } catch (IOException e) {
            System.err.println("Error loading data into database");
            System.err.println(e.getMessage());
            fail();
        }
    }

    private boolean tableExists(Connection conn, String table) throws SQLException {
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, table);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    @Test
    void testTablesCreated() throws Exception {
        try (Connection conn = DBUtil.getConnection()) {
            assertTrue(tableExists(conn, "users"));
            assertTrue(tableExists(conn, "Rooms"));
            assertTrue(tableExists(conn, "Reservations"));
            assertTrue(tableExists(conn, "ReservedRooms"));
            assertTrue(tableExists(conn, "ProductDescriptions"));
        }
    }

    @Test
    void createReservation() throws SQLException {
        DBUtil.setTestDbDirectory("src/test/resources");
        ReservationService reservationService = new ReservationService();
        RoomService roomService = new RoomService();
        try (Connection conn = DBUtil.getConnection()) {
            Room testRoom = roomService.findRoom(201);
            List<Room> roomList = List.of(testRoom);
            String newID = reservationService.createReservation(roomList, LocalDate.of(2030, 12, 1),
                    LocalDate.of(2030, 12, 2),
                    testRoom.getRate(), "Tester", "999");



            assertEquals("Tester", reservationService.findReservation(newID).getName());
        }
    }

    @Test
    void findValidReservation() throws SQLException {
        DBUtil.setTestDbDirectory("src/test/resources");
        ReservationService reservationService = new ReservationService();
        try (Connection conn = DBUtil.getConnection()) {
           Reservation testReserve = reservationService.findReservation("R-001");
           assertEquals(1, testReserve.getRooms().size());
           assertEquals(101, testReserve.getRooms().get(0).getRoomNo());
           assertEquals(LocalDate.of(2026, 12, 1), testReserve.getDateRange().startDate());
        }
    }

    @Test
    void findInvalidReservation() throws SQLException {
        DBUtil.setTestDbDirectory("src/test/resources");
        ReservationService reservationService = new ReservationService();
        assertNull(reservationService.findReservation("R-999"));
    }

    @Test
    void modifyExistingReservation() throws SQLException {
        DBUtil.setTestDbDirectory("src/test/resources");
        ReservationService reservationService = new ReservationService();
        reservationService.modifyReservation("R-002", LocalDate.of(2027, 10, 10),
                LocalDate.of(2027, 10, 12), "Tester");

        Reservation testReserve =  reservationService.findReservation("R-002");
        assertEquals("Tester", testReserve.getName());
        assertEquals(LocalDate.of(2027, 10, 10), testReserve.getDateRange().startDate());
    }

    @Test
    void deleteExistingReservation() throws SQLException {
        DBUtil.setTestDbDirectory("src/test/resources");
        ReservationService reservationService = new ReservationService();
        reservationService.deleteReservation("R-010");

        assertNull(reservationService.findReservation("R-010"));
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        assertTrue(DBUtil.deleteDatabase("src/test/resources/golden.db"));
    }
}
