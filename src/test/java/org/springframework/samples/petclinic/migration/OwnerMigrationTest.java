package org.springframework.samples.petclinic.migration;

import org.junit.jupiter.api.*;

import org.mockito.Mockito;
import org.springframework.samples.petclinic.owner.Owner;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OwnerMigrationTest {

    private static OwnerMigration ownerMigration;
    private static List<Owner> oldDataStoreOwners;

    static Owner owner1;
    static Owner owner2;
    static Owner owner3;
    static Owner owner4;

    @BeforeAll
    static void setup(){
        MigrationToggles.isUnderTest = true;

        ownerMigration = new OwnerMigration();

        owner1 = Mockito.mock(Owner.class);
        owner2 = Mockito.mock(Owner.class);
        owner3 = Mockito.mock(Owner.class);
        owner4 = Mockito.mock(Owner.class);
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

        when(owner3.getId()).thenReturn(3);
        when(owner3.getFirstName()).thenReturn("Eduardo");
        when(owner3.getLastName()).thenReturn("Rodriquez");
        when(owner3.getAddress()).thenReturn("2693 Commerce St.");
        when(owner3.getCity()).thenReturn("McFarland");
        when(owner3.getTelephone()).thenReturn("6085558763");

        when(owner4.getId()).thenReturn(4);
        when(owner4.getFirstName()).thenReturn("Harold");
        when(owner4.getLastName()).thenReturn("Davis");
        when(owner4.getAddress()).thenReturn("563 Friendly St.");
        when(owner4.getCity()).thenReturn("Windsor");
        when(owner4.getTelephone()).thenReturn("6085553198");

        oldDataStoreOwners = new ArrayList<>();

        oldDataStoreOwners.add(owner1);
        oldDataStoreOwners.add(owner2);
    }



    @Test
    @Order(1)
    public void testForklift()  {
      assertEquals(2,ownerMigration.forkliftTestOnly(oldDataStoreOwners));
    }

    @Test
    @Order(2)
    public void testCheckConsistency(){
        oldDataStoreOwners.add(owner3);

        assertEquals(1,ownerMigration.checkConsistenciesTestOnly(oldDataStoreOwners));
    }

    @Test
    @Order(3)
    public void testShadowReadWriteConsistencyChecker(){
        oldDataStoreOwners.add(owner4);

        ownerMigration.shadowWriteToNewDatastore(owner4);

        assertTrue(ownerMigration.shadowReadWriteConsistencyChecker(owner4));

    }

    @AfterAll
    public static void closeConnection() throws SQLException {
        ownerMigration.closeConnections();
    }
}
