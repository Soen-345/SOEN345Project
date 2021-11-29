package org.springframework.samples.petclinic.migration;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.samples.petclinic.vet.Specialty;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SpecialtiesMigrationTest {

    private static SpecialtiesMigration specialtiesMigration;
    private static List<Specialty> oldDataStoreVets;

    static Specialty specialty1;
    static Specialty specialty2;
    static Specialty specialty3;
    static Specialty specialty4;
    static Specialty specialty5;

    @BeforeAll
    public static void setup() {

        MigrationToggles.isUnderTest = true;

        specialtiesMigration = new SpecialtiesMigration();

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




        oldDataStoreVets = new ArrayList<>();

        oldDataStoreVets.add(specialty1);
        oldDataStoreVets.add(specialty2);
        oldDataStoreVets.add(specialty3);

    }

    @Test()
    @Order(1)
    public void testForklift() {

        assertEquals(3, specialtiesMigration.forkliftTestOnly(oldDataStoreVets));

    }

    @Test
    @Order(2)
    public void testCheckConsistency() {

        oldDataStoreVets.add(specialty4);


        assertEquals(1, specialtiesMigration.checkConsistenciesTestOnly(oldDataStoreVets));
    }

    @Test
    @Order(3)
    public void testShadowReadConsistencyChecker() {

        oldDataStoreVets.add(specialty5);


        specialtiesMigration.shadowWrite(specialty5);


        assertTrue(specialtiesMigration.shadowReadWriteConsistencyChecker(specialty5));


    }

    @AfterAll
    public static void closeConnection() throws SQLException {
        specialtiesMigration.closeConnections();
    }


}
