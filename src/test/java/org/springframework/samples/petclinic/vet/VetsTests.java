package org.springframework.samples.petclinic.vet;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class VetsTests {

     @Test
     void TestGetVetList(){

         Vets vets = new Vets();
         Vet vet1 = Mockito.mock(Vet.class);
         Vet vet2 = Mockito.mock(Vet.class);
         Specialty specialty = Mockito.mock(Specialty.class);
         Specialty specialty2 = Mockito.mock(Specialty.class);

         when(vet1.getFirstName()).thenReturn("James");
         when(vet2.getFirstName()).thenReturn("Helen");
         when(specialty.getName()).thenReturn("radiology");
         when(specialty2.getName()).thenReturn("surgery");
         vet1.addSpecialty(specialty);
         vet2.addSpecialty(specialty2);
         vets.getVetList().add(vet1);
         vets.getVetList().add(vet2);
         assertEquals(vet1,vets.getVetList().get(0));
         assertEquals(vet2,vets.getVetList().get(1));



     }
}
