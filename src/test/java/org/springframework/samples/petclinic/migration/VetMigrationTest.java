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

    private static final String SQLite_URL_TEST = "jdbc:sqlite:test-pet-clinic";

    private Connection testDbConnection;
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

        try {
            testDbConnection = DriverManager.getConnection(SQLite_URL_TEST);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        MigrationToggles.isUnderTest = true;


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

    @Test
    @Order(1)
    public void testForklift() throws SQLException {

        VetMigration.forklift(oldDataStoreVets);

        if (testDbConnection != null) {
            Statement statement = testDbConnection.createStatement();

            assertTrue(statement.execute("SELECT * FROM vets"));
        }


    }

    @Test
    @Order(2)
    public void testCheckConsistency() {

        oldDataStoreVets.put(vet4.getId(), vet4);
        assertEquals(1, VetMigration.checkConsistencies(oldDataStoreVets));

    }

    @Test
    @Order(3)
    public void testShadowReadConsistencyChecker() {

        oldDataStoreVets.put(vet5.getId(), vet5);

        VetMigration.addVetToNewDatastore(vet5);

        assertEquals(0, VetMigration.checkConsistencies(oldDataStoreVets));



    }

    @Test
    @Order(4)
    public void testCorrectConsistency() {

        when(vet3.getFirstName()).thenReturn("Lola");

        VetMigration.correctInconsistency(this.vet3);

        assertEquals(0, VetMigration.checkConsistencies(oldDataStoreVets));

    }




}
