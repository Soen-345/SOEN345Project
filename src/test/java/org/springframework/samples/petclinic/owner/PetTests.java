package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.samples.petclinic.visit.Visit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author alireza ziairiz
 * @author  Beshoy
 */
public class PetTests {
    private Pet pet;
    private Owner owner;
    private Visit visit;
    private PetType type;
    @Mock
    private HashSet<Visit> setOfVisits;

    @BeforeEach
    public void setup(){
        owner = mock(Owner.class);
        visit = mock(Visit.class);
        type =  mock(PetType.class);
        pet = new Pet("max", type, owner);
        pet.addVisit(visit);
        setOfVisits = new HashSet<>();
        setOfVisits.add(visit);
        when(visit.getPetId()).thenReturn(123);
    }

    @Test
    void testPet(){
     pet.setVisitsInternal(setOfVisits);
     verify(visit).setPetId(pet.getId());
     assertEquals(123,(int) visit.getPetId());

    }





}