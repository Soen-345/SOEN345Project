package org.springframework.samples.petclinic.migration;

import org.sqlite.SQLiteConnection;

import java.sql.*;

public class DatastoreConnection {

    private static final String SQLite_URL = "jdbc:sqlite:pet-clinic";
    private static final String SQLite_URL_TEST = "jdbc:sqlite:test-pet-clinic";
    private static final String H2_URL = "jdbc:h2:mem:testdb";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    private static Connection SQLite_CONNECTION;
    private  static Connection SQLite_TEST_CONNECTION;
    private static Connection H2_CONNECTION;



    protected static Connection connectSqlite() {
        Connection conn = null;
        if (MigrationToggles.isUnderTest) {
            try {
                if (SQLite_TEST_CONNECTION != null) {
                    conn = SQLite_TEST_CONNECTION;
                    System.out.println("***Returning existing connection to test SQLite***");
                }
                else {

                    conn = DriverManager.getConnection(SQLite_URL_TEST);
                    SQLite_TEST_CONNECTION = conn;
                    System.out.println("***Connection to test SQLite successful***");
                }



            }
            catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        else {
            try {

                if (SQLite_CONNECTION != null) {
                    conn = SQLite_CONNECTION;
                    System.out.println("***Returning existing connection to SQLite***");
                }
                else {
                    conn = DriverManager.getConnection(SQLite_URL);
                    SQLite_CONNECTION = conn;
                    System.out.println("***Connection to SQLite successful***");
                }

            }
            catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        return conn;
    }

    protected static Connection connectH2() {
        Connection conn = null;
        try {
            if (H2_CONNECTION != null) {
                conn = H2_CONNECTION;
                System.out.println("***Returning existing connection to H2***");
            }
            else {
                conn = DriverManager.getConnection(H2_URL, USERNAME, PASSWORD);
                H2_CONNECTION = conn;
                System.out.println("***Connection to H2 successful***");
            }


        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

}
