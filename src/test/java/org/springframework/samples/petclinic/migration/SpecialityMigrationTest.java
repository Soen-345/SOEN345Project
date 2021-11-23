package org.springframework.samples.petclinic.migration;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.samples.petclinic.vet.Specialty;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SpecialityMigrationTest {
    private static SpecialtyMigration specialtyMigration;

    private static Map<Integer, Specialty> oldDataStoreVets;

    static Specialty specialty1;
    static Specialty specialty2;
    static Specialty specialty3;
    static Specialty specialty4;
    static Specialty specialty5;

    @BeforeAll
    public static void setup() {

        MigrationToggles.isUnderTest = true;

        specialtyMigration = new SpecialtyMigration();

        specialty1 = Mockito.mock(Specialty.class);
        specialty2 = Mockito.mock(Specialty.class);
        specialty3 = Mockito.mock(Specialty.class);
        specialty4 = Mockito.mock(Specialty.class);
        specialty5 = Mockito.mock(Specialty.class);

        when(specialty1.getId()).thenReturn(1);
        when(specialty1.getName()).thenReturn("none");

        when(specialty2.getId()).thenReturn(2);
        when(specialty2.getName()).thenReturn("radiology");

        when(specialty3.getId()).thenReturn(3);
        when(specialty3.getName()).thenReturn("dentistry");

        when(specialty4.getId()).thenReturn(4);
        when(specialty4.getName()).thenReturn("Surgery");

        when(specialty5.getId()).thenReturn(5);
        when(specialty5.getName()).thenReturn("none");




        oldDataStoreVets = new HashMap<>();

        oldDataStoreVets.put(specialty1.getId(), specialty1);
        oldDataStoreVets.put(specialty2.getId(), specialty2);
        oldDataStoreVets.put(specialty3.getId(), specialty3);

    }

    @Test()
    @Order(1)
    public void testForklift() {

        assertEquals(3, specialtyMigration.forkliftTestOnly(oldDataStoreVets));

    }

    @Test
    @Order(2)
    public void testCheckConsistency() {

        oldDataStoreVets.put(specialty4.getId(), specialty4);


        assertEquals(1, specialtyMigration.checkConsistenciesTestOnly(oldDataStoreVets));
    }

    @Test
    @Order(3)
    public void testShadowReadConsistencyChecker() {

        oldDataStoreVets.put(specialty5.getId(), specialty5);


        specialtyMigration.shadowWrite(specialty5);


        assertTrue(specialtyMigration.shadowReadConsistencyChecker(specialty5));


    }

    @AfterAll
    public static void closeConnection() throws SQLException {
        specialtyMigration.closeConnections();
    }


}
