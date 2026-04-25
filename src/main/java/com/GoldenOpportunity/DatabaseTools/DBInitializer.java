package com.GoldenOpportunity.DatabaseTools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static com.GoldenOpportunity.DatabaseTools.DBUtil.*;


/**
 * Responsibilities: Load the database with all required tables
 */
public class DBInitializer {
/**
 * Guest must have their reservations either in the User Database or a separate database
 */

    /**
     * Responsibilities:
     * - Define where the database file lives on disk (relative path under {@code data/}).
     * - Provide a JDBC {@link Connection} configured with sane defaults (e.g., foreign keys ON).
     * - Ensure the required schema exists the first time the app runs.
     *
     * Why this class exists: keeping JDBC URL and schema creation in one place makes it easier
     * to migrate or swap implementations later (e.g., move to SQLCipher, replace SQLite, etc.).
     */
        /** Folder that contains the SQLite file. Relative to the project run directory. */

    /**
     * These are all the valid input files which are to be referenced upon
     * initialization of the database. They use they
     */

    private static final String roomFile = "src/main/resources/room_insertWBedTypes.sql";
        private static final String reservationFile = "src/main/resources/reservation_insert.sql";
        private static final String reservedRoomsFile = "src/main/resources/reservedRooms_insert.sql";
        private static final String shopFile = "src/main/resources/shop.sql";


    /**
     * A list of all valid table names.
     */
    private static final List<String> tableNames =  List.of("users", "Rooms", "Reservations", "ReservedRooms",
                "ProductDescriptions");

        private DBInitializer() {}


        /*public static Connection getConnection() throws SQLException {
            Connection conn = DBUtil.getConnection();
            // Enforce referential integrity for this connection.
            try (Statement st = conn.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }
            return conn;
        } */

        /**
         * Ensures the database folder exists and creates the schema if missing.
         * Intended to be called once at application startup.
         */
        public static void initialize() throws SQLException, IOException {
            //ensureDbFolder(); //TODO: Removed as it seemed unnecessary given the database will be
            //TODO: created regardless
            // Opening a connection will also create the file on first use.
            try (Connection conn = getConnection()) {
                DBUtil.ensureDbFolder();
                createSchema(conn);
                loadData(conn);
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }

        //TODO: Depreciated, moved to DBUtil
        /** Create the data folder and an empty DB file path if needed. */
        /*
        private static void ensureDbFolder() {
            try {
                Path dir = Paths.get(DB_DIR);
                if (!Files.exists(dir)) {
                    Files.createDirectories(dir);
                }
                // Touch the DB file path so that the location exists and is visible to the user.
                File dbFile = dir.resolve(DB_FILE).toFile();
                if (!dbFile.exists()) {
                    dbFile.createNewFile();
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to prepare database directory", e);
            }
        } */

        /** Creates all tables for the table do not already exist. */
        private static void createSchema(Connection conn) throws SQLException {
            // Users table stores credentials (hashed), role, and lockout metadata.
            String createUsers = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password_hash TEXT NOT NULL,
                role TEXT NOT NULL,
                account_status TEXT NOT NULL DEFAULT 'ACTIVE',
                failed_login_count INTEGER NOT NULL DEFAULT 0,
                contact_info TEXT,
                created_at TEXT NOT NULL,
                updated_at TEXT NOT NULL
            );
        """;

            String createUniqueEmailIndex = """
            CREATE UNIQUE INDEX IF NOT EXISTS idx_users_contact_info_unique
            ON users(contact_info)
            WHERE contact_info IS NOT NULL AND contact_info <> '';
        """;

            String createRooms = """
                CREATE TABLE IF NOT EXISTS Rooms (
                    floorNum   INTEGER NOT NULL,
                    roomNo     INTEGER PRIMARY KEY,
                    numBeds    INTEGER NOT NULL,
                    smoking    BOOLEAN NOT NULL,
                    qLevel     TEXT NOT NULL,
                    roomType   TEXT NOT NULL,
                    rate       REAL NOT NULL,
                    bedTypes   TEXT NOT NULL
                );
                """;

            String createReservations = """
                CREATE TABLE IF NOT EXISTS Reservations (
                      resId TEXT PRIMARY KEY,
                      startDate TEXT NOT NULL,
                      endDate TEXT NOT NULL,
                      bill REAL NOT NULL,
                      checkedIn BOOLEAN NOT NULL
                  );
                """;

            String createReservedRooms = """
                CREATE TABLE IF NOT EXISTS ReservedRooms (
                      resId TEXT NOT NULL,
                      roomNo INTEGER NOT NULL,
                      floorNum INTEGER NOT NULL,
                      PRIMARY KEY (resId, roomNo),
                      FOREIGN KEY (resId) REFERENCES Reservations(resId),
                      FOREIGN KEY (roomNo) REFERENCES Rooms(roomNo)
                );
                """;
            String createProductDescriptions = """
                    CREATE TABLE IF NOT EXISTS ProductDescriptions (
                        productID INTEGER PRIMARY KEY,
                        name TEXT NOT NULL,
                        price REAL NOT NULL,
                        stock INTEGER NOT NULL,
                        image TEXT NOT NULL,
                        description TEXT
                    );
                    """;

            try (Statement st = conn.createStatement()) {
                st.execute(createUsers);
                st.execute(createUniqueEmailIndex);
                st.execute(createRooms);
                st.execute(createReservations);
                st.execute(createReservedRooms);
                st.execute(createProductDescriptions);
            }
        }

    /**
     * Loads all the data from the input files into the created tables
     * @param conn
     * @throws SQLException
     * @throws IOException
     */
    private static void loadData(Connection conn) throws SQLException, IOException {
                DBLoader.loadData(conn, roomFile, tableNames.get(tableNames.indexOf("Rooms")));
                DBLoader.loadData(conn, reservationFile, tableNames.get(tableNames.indexOf("Reservations")));
                DBLoader.loadData(conn, reservedRoomsFile, tableNames.get(tableNames.indexOf("ReservedRooms")));
                DBLoader.loadData(conn, shopFile, tableNames.get(tableNames.indexOf("ProductDescriptions")));
        }
    }
