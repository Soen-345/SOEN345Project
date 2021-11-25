package org.springframework.samples.petclinic.migration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.samples.petclinic.vet.VetSpecialties;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VetSpecialtiesTest {
    private static VetSpecialtiesMigration VetSpecialtiesMigration;
    private static Map<Integer, VetSpecialties> oldDataStoreVetSpecialties;

    static VetSpecialties vetspe1;
    static VetSpecialties vetspe2;
    static VetSpecialties vetspe3;

    @BeforeEach
    public void setup(){
        MigrationToggles.isUnderTest = true;
        VetSpecialtiesMigration = new VetSpecialtiesMigration();
        vetspe1 = Mockito.mock(VetSpecialties.class);
        vetspe2 = Mockito.mock(VetSpecialties.class);
        vetspe3 = Mockito.mock(VetSpecialties.class);

        // vetspecialties 1 data
        when(vetspe1.getVet_id()).thenReturn(2);
        when(vetspe1.getSpecialty_id()).thenReturn(1);

        when(vetspe2.getVet_id()).thenReturn(3);
        when(vetspe2.getSpecialty_id()).thenReturn(2);

        when(vetspe3.getVet_id()).thenReturn(3);
        when(vetspe3.getSpecialty_id()).thenReturn(3);

        oldDataStoreVetSpecialties = new HashMap<>();
        oldDataStoreVetSpecialties.put(vetspe1.getVet_id(),vetspe1);
        oldDataStoreVetSpecialties.put(vetspe2.getVet_id(),vetspe2);
    }


    @Test
    public void testforklift() throws SQLException {
        assertEquals(2,VetSpecialtiesMigration.forkliftTestOnly(oldDataStoreVetSpecialties));
    }

}
