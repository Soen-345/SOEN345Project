package org.springframework.samples.petclinic.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteConnection;

import java.sql.*;

public class DatastoreConnection {

    private static final Logger log = LoggerFactory.getLogger(DatastoreConnection.class);

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
                if (SQLite_TEST_CONNECTION != null && !SQLite_TEST_CONNECTION.isClosed()) {
                    conn = SQLite_TEST_CONNECTION;
                }
                else {

                    conn = DriverManager.getConnection(SQLite_URL_TEST);
                    SQLite_TEST_CONNECTION = conn;
                    log.info("Connection to test SQLite successful");
                }
            }
            catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        else {
            try {

                if (SQLite_CONNECTION != null && !SQLite_CONNECTION.isClosed()) {
                    conn = SQLite_CONNECTION;
                }
                else {
                    conn = DriverManager.getConnection(SQLite_URL);
                    SQLite_CONNECTION = conn;
                    log.info("Connection to SQLite successful");
                }

            }
            catch (SQLException e) {
                log.error(e.getMessage());
            }
        }

        return conn;
    }

    protected static Connection connectH2() {
        Connection conn = null;
        try {
            if (H2_CONNECTION != null) {
                conn = H2_CONNECTION;
            }
            else {
                conn = DriverManager.getConnection(H2_URL, USERNAME, PASSWORD);
                H2_CONNECTION = conn;
                log.info("Connection to H2 successful");
            }


        }
        catch (SQLException e) {
            log.error(e.getMessage());
        }
        return conn;
    }

}
