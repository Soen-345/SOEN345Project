package org.springframework.samples.petclinic.migration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

    private Map<Integer, Vet> oldDataStoreVets;

    @Mock
    Vet vet1;
    @Mock
    Vet vet2;
    @Mock
    Vet vet3;
    @Mock
    Vet vet4;
    @Mock
    Vet vet5;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        MigrationToggles.isUnderTest = true;

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

        assertEquals(3, vetMigration.forklift(oldDataStoreVets));

    }

    @Test
    @Order(2)
    public void testCheckConsistency() {

        oldDataStoreVets.put(vet4.getId(), vet4);


        assertEquals(1, vetMigration.checkConsistencies(oldDataStoreVets));
    }

    @Test
    @Order(3)
    public void testShadowReadConsistencyChecker() {

        oldDataStoreVets.put(vet5.getId(), vet5);


        vetMigration.shadowWrite(vet5);


        assertTrue(vetMigration.shadowReadConsistencyChecker(vet5));


    }

    @AfterAll
    public static void closeConnection() throws SQLException {
        vetMigration.closeConnections();
    }


}
