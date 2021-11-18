package org.springframework.samples.petclinic.migration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class DataMigration {



    public static void forklift() {

        try {
            FileReader reader = new FileReader("src/main/resources/db/hsqldb/data.sql");
            BufferedReader buffer = new BufferedReader(reader);

            while (buffer.readLine() != null) {
                String query = buffer.readLine();
                SqliteConnection.query(query);
            }

        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }


    }
}
