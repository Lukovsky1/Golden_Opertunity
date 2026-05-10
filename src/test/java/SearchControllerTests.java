import com.GoldenOpportunity.*;
import com.GoldenOpportunity.DatabaseTools.DBInitializer;
import com.GoldenOpportunity.DatabaseTools.DBUtil;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class SearchControllerTests {
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

    @Test
    public void findRoomsForGivenDateRange() throws SQLException {
        LocalDate startDate = LocalDate.of(2026, 11, 2);
        LocalDate endDate = LocalDate.of(2026, 11, 30);
        SearchController searchController = new SearchController(new RoomService(), new ReservationService());

        Criteria crit = new Criteria();
        crit.setDateRange(new DateRange(startDate, endDate));

        List<Room> rooms = searchController.searchAvailableRooms(crit);

        assertEquals(11, rooms.size());
    }

    @Test
    public void findRoomsForFloorNumberAndQuality() throws SQLException {
        SearchController searchController = new SearchController(new RoomService(), new ReservationService());
        Criteria crit = new Criteria();

        crit.setFloorNum(1);
        crit.setQuality("Economic");

        List<Room> rooms = searchController.searchAvailableRooms(crit);
        assertEquals(3, rooms.size());
        assertEquals(101, rooms.get(0).getRoomNo());
    }

    @Test
    public void noAvailableRooms() throws SQLException {
        SearchController searchController = new SearchController(new RoomService(), new ReservationService());
        Criteria crit = new Criteria();
        crit.setFloorNum(1);
        crit.setSmoking(true);

        List<Room> rooms = searchController.searchAvailableRooms(crit);
        assertEquals(0, rooms.size());
    }

    @Test
    public void createNewReservation() throws SQLException {
        ReservationService reservationService = new ReservationService();
        SearchController searchController = new SearchController(new RoomService(), reservationService);

        Criteria crit = new Criteria();
        LocalDate startDate = LocalDate.of(2028, 11, 2);
        LocalDate endDate = LocalDate.of(2028, 11, 5);

        crit.setDateRange(new DateRange(startDate, endDate));
        crit.setRoomNum(101);

        List<Room> roomsForRes = searchController.searchAvailableRooms(crit);
        crit.setRoomNum(201);

        roomsForRes.add(searchController.searchAvailableRooms(crit).get(0));


        String newId = reservationService.createReservation(roomsForRes,
                startDate, endDate, 200, "Tester", "999");

        assertEquals(2, reservationService.findReservation(newId).getRooms().size());


    }


    @AfterAll
    public static void tearDown() throws SQLException {
        assertTrue(DBUtil.deleteDatabase("src/test/resources/golden.db"));
    }
}
