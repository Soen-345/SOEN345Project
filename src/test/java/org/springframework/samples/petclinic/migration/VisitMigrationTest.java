package org.springframework.samples.petclinic.migration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.samples.petclinic.visit.Visit;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sevag Eordkian
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VisitMigrationTest {

    private static VisitMigration visitMigration;

    private Map<Integer, Visit> oldDataStoreVisits;

    @Mock
    Visit visit1;
    @Mock
    Visit visit2;
    @Mock
    Visit visit3;
    @Mock
    Visit visit4;
    @Mock
    Visit visit5;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        MigrationToggles.isUnderTest = true;

        visitMigration = new VisitMigration();

        Date date = new Date();

        when(visit1.getId()).thenReturn(12);
        when(visit1.getPetId()).thenReturn(5);
        when(visit1.getDate()).thenReturn(VisitMigration.convertToLocalDateViaInstant(date));
        when(visit1.getDescription()).thenReturn("General check");

        when(visit2.getId()).thenReturn(13);
        when(visit2.getPetId()).thenReturn(6);
        when(visit2.getDate()).thenReturn(VisitMigration.convertToLocalDateViaInstant(date));
        when(visit2.getDescription()).thenReturn("First Vaccine");

        when(visit3.getId()).thenReturn(14);
        when(visit3.getPetId()).thenReturn(7);
        when(visit3.getDate()).thenReturn(VisitMigration.convertToLocalDateViaInstant(date));
        when(visit3.getDescription()).thenReturn("Emergency");

        when(visit4.getId()).thenReturn(15);
        when(visit4.getPetId()).thenReturn(8);
        when(visit4.getDate()).thenReturn(VisitMigration.convertToLocalDateViaInstant(date));
        when(visit4.getDescription()).thenReturn("Second Vaccine");

        when(visit5.getId()).thenReturn(16);
        when(visit5.getPetId()).thenReturn(9);
        when(visit5.getDate()).thenReturn(VisitMigration.convertToLocalDateViaInstant(date));
        when(visit5.getDescription()).thenReturn("Just a visit");

        oldDataStoreVisits = new HashMap<>();

        oldDataStoreVisits.put(visit1.getId(), visit1);
        oldDataStoreVisits.put(visit2.getId(), visit2);
        oldDataStoreVisits.put(visit3.getId(), visit3);

    }

    @Test()
    @Order(1)
    public void testForklift() {

        assertEquals(3, visitMigration.forkliftTestOnly(oldDataStoreVisits));

    }

    @Test
    @Order(2)
    public void testCheckConsistency() {

        oldDataStoreVisits.put(visit4.getId(), visit4);


        assertEquals(1, visitMigration.checkConsistenciesTestOnly(oldDataStoreVisits));
    }

    @Test
    @Order(3)
    public void testShadowReadConsistencyChecker() {

        oldDataStoreVisits.put(visit5.getId(), visit5);


        visitMigration.shadowWrite(visit5);


        assertTrue(visitMigration.shadowReadConsistencyChecker(visit5));


    }

    @AfterAll
    public static void closeConnection() throws SQLException {
        visitMigration.closeConnections();
    }

}
