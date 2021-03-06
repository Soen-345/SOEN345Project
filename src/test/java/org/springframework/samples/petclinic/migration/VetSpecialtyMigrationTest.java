package org.springframework.samples.petclinic.migration;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.samples.petclinic.vet.VetSpecialty;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VetSpecialtyMigrationTest {

    private static VetSpecialtiesMigration VetSpecialtiesMigration;
    private static List<VetSpecialty> oldDataStoreVetSpecialties;

    static VetSpecialty vetspe1;
    static VetSpecialty vetspe2;
    static VetSpecialty vetspe3;
    static VetSpecialty vetspe4;

    @BeforeAll
    static void setup(){
        MigrationToggles.isUnderTest = true;

        VetSpecialtiesMigration = new VetSpecialtiesMigration();

        vetspe1 = Mockito.mock(VetSpecialty.class);
        vetspe2 = Mockito.mock(VetSpecialty.class);
        vetspe3 = Mockito.mock(VetSpecialty.class);
        vetspe4 = Mockito.mock(VetSpecialty.class);

        // vetspecialties 1 data
        when(vetspe1.getVet_id()).thenReturn(2);
        when(vetspe1.getSpecialty_id()).thenReturn(1);

        when(vetspe2.getVet_id()).thenReturn(3);
        when(vetspe2.getSpecialty_id()).thenReturn(2);

        when(vetspe3.getVet_id()).thenReturn(3);
        when(vetspe3.getSpecialty_id()).thenReturn(3);

        when(vetspe4.getVet_id()).thenReturn(4);
        when(vetspe4.getSpecialty_id()).thenReturn(2);

        oldDataStoreVetSpecialties = new ArrayList<>();

        oldDataStoreVetSpecialties.add(vetspe1);
        oldDataStoreVetSpecialties.add(vetspe2);
    }


    @Test
    @Order(1)
    public void testForklift() {
        assertEquals(2,VetSpecialtiesMigration.forkliftTestOnly(oldDataStoreVetSpecialties));
    }

    @Test
    @Order(2)
    public void testCheckConsistency(){
        oldDataStoreVetSpecialties.add(vetspe3);
        assertEquals(1,VetSpecialtiesMigration.checkConsistenciesTestOnly(oldDataStoreVetSpecialties));

    }

    @AfterAll
    public static void closeConnection() throws SQLException {
        VetSpecialtiesMigration.closeConnections();
    }

}
