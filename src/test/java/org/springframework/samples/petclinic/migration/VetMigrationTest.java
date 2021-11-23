package org.springframework.samples.petclinic.migration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.samples.petclinic.vet.Vet;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Sevag Eordkian
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VetMigrationTest {

    private static VetMigration vetMigration;

    private static Map<Integer, Vet> oldDataStoreVets;

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


        oldDataStoreVets = new HashMap<>();

        oldDataStoreVets.put(vet1.getId(), vet1);
        oldDataStoreVets.put(vet2.getId(), vet2);
        oldDataStoreVets.put(vet3.getId(), vet3);

    }

    @Test()
    @Order(1)
    public void testForklift() {

        assertEquals(3, vetMigration.forkliftTestOnly(oldDataStoreVets));

    }

    @Test
    @Order(2)
    public void testCheckConsistency() {

        oldDataStoreVets.put(vet4.getId(), vet4);


        assertEquals(1, vetMigration.checkConsistenciesTestOnly(oldDataStoreVets));
    }

    @Test
    @Order(3)
    public void testShadowReadConsistencyChecker() {

        oldDataStoreVets.put(vet5.getId(), vet5);


        vetMigration.shadowWriteToNewDatastore(vet5);


        assertTrue(vetMigration.shadowReadConsistencyChecker(vet5));


    }

    @AfterAll
    public static void closeConnection() throws SQLException {
        vetMigration.closeConnections();
    }


}
