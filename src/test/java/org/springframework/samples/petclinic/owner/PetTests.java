package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.samples.petclinic.visit.Visit;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PetTests {

    @Test
    void testPet(){
        Pet pet = new Pet();
        Owner owner = Mockito.mock(Owner.class);

        Visit visit = Mockito.mock(Visit.class);
        PetType type = Mockito.mock(PetType.class);
        pet.setOwner(owner);

        pet.setType(type);
        pet.setId(001);
        pet.addVisit(visit);
        verify(visit).setPetId(001);
        when(owner.getCity()).thenReturn("Madison");
        assertEquals("Madison", pet.getOwner().getCity());
    }





}
