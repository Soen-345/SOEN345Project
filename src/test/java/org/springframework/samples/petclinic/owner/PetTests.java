package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.samples.petclinic.visit.Visit;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.mockito.Mockito.verify;

public class PetTests {

    @Test
    void testPet(){
        Pet pet = new Pet();
        Owner owner = Mockito.mock(Owner.class);
        Visit visit = Mockito.mock(Visit.class);
        PetType type = Mockito.mock(PetType.class);
        pet.setOwner(owner);
        pet.setType(type);
        pet.setOwner(owner);
        pet.setId(001);
        pet.addVisit(visit);
        verify(visit).setPetId(001);
    }





}
