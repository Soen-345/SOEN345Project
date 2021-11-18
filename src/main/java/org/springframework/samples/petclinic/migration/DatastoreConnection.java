package org.springframework.samples.petclinic.migration;

import java.sql.*;

public class DatastoreConnection {

    private static final String SQLite_URL = "jdbc:sqlite:pet-clinic";
    private static final String H2_URL = "jdbc:h2:mem:testdb";



    protected static Connection connectSqlite() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(SQLite_URL);

            System.out.println("Connection to SQLite successful");

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    protected static Connection connectH2() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(H2_URL);

            System.out.println("Connection to H2 successful");

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

}
