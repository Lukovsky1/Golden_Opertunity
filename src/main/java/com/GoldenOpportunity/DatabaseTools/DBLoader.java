package com.GoldenOpportunity.DatabaseTools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class is used to initialize our database with all files in the resources
 * folder
 */
public class DBLoader {
    public DBLoader() {}

    public static void loadData(Connection con, String fileName) throws SQLException, IOException {
        con.setAutoCommit(false);
        Statement st = con.createStatement();
        try {
            st.executeUpdate(Files.readString(Path.of(fileName)));
            con.commit();
        } catch (SQLException ex) {
            System.err.println("SQL Error reading file");
            throw ex;
        } catch (IOException ex) {
            System.err.println("Error reading room insert file: " + fileName );
            throw ex;
        }
    }
}
