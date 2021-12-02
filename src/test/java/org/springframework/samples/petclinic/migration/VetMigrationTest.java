package org.springframework.samples.petclinic.migration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.samples.petclinic.vet.Vet;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Sevag Eordkian
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VetMigrationTest {

    private static VetMigration vetMigration;
    private static List<Vet> oldDataStoreVets;

    static Vet vet1;
    static Vet vet2;
    static Vet vet3;
    static Vet vet4;
    static Vet vet5;

    @BeforeAll
    public static void setup() {

        MigrationToggles.isUnderTest = true;

        vetMigration = new VetMigration();

        vet1 = Mockito.mock(Vet.class);
        vet2 = Mockito.mock(Vet.class);
        vet3 = Mockito.mock(Vet.class);
        vet4 = Mockito.mock(Vet.class);
        vet5 = Mockito.mock(Vet.class);

        when(vet1.getId()).thenReturn(1);
        when(vet1.getFirstName()).thenReturn("James");
        when(vet1.getLastName()).thenReturn("Carter");

        when(vet2.getId()).thenReturn(2);
        when(vet2.getFirstName()).thenReturn("Helen");
        when(vet2.getLastName()).thenReturn("Leary");

        when(vet3.getId()).thenReturn(3);
        when(vet3.getFirstName()).thenReturn("Linda");
        when(vet3.getLastName()).thenReturn("Douglas");

        when(vet4.getId()).thenReturn(4);
        when(vet4.getFirstName()).thenReturn("Ted");
        when(vet4.getLastName()).thenReturn("Lasso");

        when(vet5.getId()).thenReturn(5);
        when(vet5.getFirstName()).thenReturn("Micheal");
        when(vet5.getLastName()).thenReturn("Scott");


        oldDataStoreVets = new ArrayList<>();

        oldDataStoreVets.add(vet1);
        oldDataStoreVets.add(vet2);
        oldDataStoreVets.add(vet3);

    }

    @Test()
    @Order(1)
    public void testForklift() {

        assertEquals(3, vetMigration.forkliftTestOnly(oldDataStoreVets));

    }

    @Test
    @Order(2)
    public void testCheckConsistency() {

        oldDataStoreVets.add(vet4);


        assertEquals(1, vetMigration.checkConsistenciesTestOnly(oldDataStoreVets));
    }

    @Test
    @Order(3)
    public void testShadowReadConsistencyChecker() {

        oldDataStoreVets.add(vet5);


        vetMigration.shadowWriteToNewDatastore(vet5);


        assertTrue(vetMigration.shadowReadWriteConsistencyChecker(vet5));


    }

    @AfterAll
    public static void closeConnection() throws SQLException {
        vetMigration.closeConnections();
    }


}
