package org.springframework.samples.petclinic.migration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class SqliteConnection {


    private static Connection connect() {
        Connection conn = null;
        try {
            String url = "your sqlite url";
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite successful");

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }


    public static void query(String query) {
        Connection conn = connect();
        if (conn != null) {
            try {
                Statement statement = conn.createStatement();
                ResultSet queryResult = statement.executeQuery(query);

                while (queryResult.next()) {
                    System.out.println(queryResult.getInt("id") + "\t" +
                            queryResult.getString("first_name") + "\t" +
                            queryResult.getString("last_name"));
                }
            }
            catch(SQLException e) {
                System.out.println(e.getMessage());
            }
            finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }

            }
        }

    }
}
