package org.springframework.samples.petclinic.migration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.samples.petclinic.vet.Vet;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sevag Eordkian
 */

public class VetMigrationTest {

    private static final String SQLite_URL_TEST = "jdbc:sqlite:test-pet-clinic";
    private VetMigration vetMigration;
    private Connection testDbConnection;
    private Map<Integer, Vet> oldDataStoreVets;

    @Mock
    Vet vet1;
    @Mock
    Vet vet2;
    @Mock
    Vet vet3;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        DatastoreToggles.isUnderTest = true;

        vetMigration = new VetMigration();

        when(vet1.getId()).thenReturn(1);
        when(vet1.getFirstName()).thenReturn("James");
        when(vet1.getLastName()).thenReturn("Carter");

        when(vet2.getId()).thenReturn(2);
        when(vet2.getFirstName()).thenReturn("Helen");
        when(vet2.getLastName()).thenReturn("Leary");

        when(vet3.getId()).thenReturn(3);
        when(vet3.getFirstName()).thenReturn("Linda");
        when(vet3.getLastName()).thenReturn("Douglas");

        oldDataStoreVets = new HashMap<>();

        oldDataStoreVets.put(vet1.getId(), vet1);
        oldDataStoreVets.put(vet2.getId(), vet2);
        oldDataStoreVets.put(vet3.getId(), vet3);

    }

    @Test
    public void testForklift() throws SQLException {


        vetMigration.forklift(oldDataStoreVets);
        testDbConnection = DriverManager.getConnection(SQLite_URL_TEST);

        if (testDbConnection != null) {
            Statement statement = testDbConnection.createStatement();

            assertTrue(statement.execute("SELECT * FROM vets"));
        }


    }

    @Test
    public void testCheckConsistency() {

        assertEquals(0, vetMigration.checkConsistencies(new HashMap<>()));
    }
}
