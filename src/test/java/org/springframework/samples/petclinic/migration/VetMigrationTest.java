package org.springframework.samples.petclinic.migration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.samples.petclinic.vet.Vet;

/**
 * @author Sevag Eordkian
 */
public class VetMigrationTest {

    private VetMigration vetMigration;

    @Mock
    Vet vet1;
    @Mock
    Vet vet2;
    @Mock
    Vet vet3;


    @BeforeEach
    public void setup() {

        vetMigration = new VetMigration();
/*
        when(this.vet1.getId()).thenReturn(1);
        when(this.vet1.getFirstName()).thenReturn("James");
        when(this.vet1.getLastName()).thenReturn("Carter");

        when(this.vet2.getId()).thenReturn(2);
        when(this.vet2.getFirstName()).thenReturn("Helen");
        when(this.vet2.getLastName()).thenReturn("Leary");

        when(this.vet3.getId()).thenReturn(3);
        when(this.vet3.getFirstName()).thenReturn("Linda");
        when(this.vet3.getLastName()).thenReturn("Douglas");
*/
    }

    @Test
    public void testForklift() {

    }

    @Test
    public void testCheckConsistency() {

    }
}
