package org.springframework.samples.petclinic.migration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.samples.petclinic.owner.Owner;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class OwnerMigrationTest {
    private static final String SQLite_URL_TEST = "jdbc:sqlite:test-pet-clinic";
    private OwnerMigration ownerMigration;
    private Connection testDbConnections;
    private Map<Integer, Owner> oldDataStoreOwners;

    @Mock
    Owner owner1;
    @Mock
    Owner owner2;


    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this);
        DatastoreToggles.isUnderTest = true;
        ownerMigration = new OwnerMigration();
        // owner 1 data
        when(owner1.getId()).thenReturn(1);
        when(owner1.getFirstName()).thenReturn("George");
        when(owner1.getLastName()).thenReturn("Franklin");
        when(owner1.getAddress()).thenReturn("110 W. Liberty St.");
        when(owner1.getCity()).thenReturn("Madison");
        when(owner1.getTelephone()).thenReturn("6085551023");

        //owner 2 data
        when(owner2.getId()).thenReturn(2);
        when(owner2.getFirstName()).thenReturn("Betty");
        when(owner2.getLastName()).thenReturn("Davis");
        when(owner2.getAddress()).thenReturn("638 Cardinal Ave.");
        when(owner2.getCity()).thenReturn("Sun Prairie");
        when(owner2.getTelephone()).thenReturn("6085551749");

        oldDataStoreOwners = new HashMap<>();

        oldDataStoreOwners.put(owner1.getId(),owner1);
        oldDataStoreOwners.put(owner2.getId(),owner2);
    }



    @Test
    public void testforklift() throws SQLException {
        ownerMigration.forklift(oldDataStoreOwners);
        testDbConnections = DriverManager.getConnection(SQLite_URL_TEST);

        if(testDbConnections != null){
            Statement statement = testDbConnections.createStatement();
            assertTrue(statement.execute("SELECT * FROM owners"));
        }
    }
}
