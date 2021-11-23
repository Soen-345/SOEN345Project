package org.springframework.samples.petclinic.migration;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.samples.petclinic.owner.Owner;
import org.mockito.MockitoAnnotations;
import org.springframework.samples.petclinic.vet.Vet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OwnerMigrationTest {
    private static OwnerMigration ownerMigration;
    private static Map<Integer,Owner> oldDataStoreOwners;

    static Owner owner1;
    static Owner owner2;


    @BeforeEach
    public void setup(){
        MigrationToggles.isUnderTest = true;
        ownerMigration = new OwnerMigration();
        owner1 = Mockito.mock(Owner.class);
        owner2 = Mockito.mock(Owner.class);
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
    @Order(1)
    public void testforklift() throws SQLException {
      assertEquals(2,ownerMigration.forkliftTestOnly(oldDataStoreOwners));
    }
}
