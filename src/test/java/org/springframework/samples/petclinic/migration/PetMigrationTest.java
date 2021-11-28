package org.springframework.samples.petclinic.migration;


import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PetMigrationTest {

    private static List<Pet> oldDataStorePets;
    private static PetMigration petMigration;
    private static TypeMigration typeMigration;

    static Pet pet1;
    static Pet pet2;
    static Pet pet3;
    static Pet pet4;
    static Pet pet5;

    static Owner owner;


    @BeforeAll
    public static void setup() {

        MigrationToggles.isUnderTest = true;
        petMigration = new PetMigration();
        typeMigration = new TypeMigration();
        typeMigration.forklift();

        Date date = new Date();
        pet1 = Mockito.mock(Pet.class);
        pet2 = Mockito.mock(Pet.class);
        pet3 = Mockito.mock(Pet.class);
        pet4 = Mockito.mock(Pet.class);
        pet5 = Mockito.mock(Pet.class);

        owner = Mockito.mock(Owner.class);

        when(owner.getId()).thenReturn(1);
        when(owner.getFirstName()).thenReturn("George");
        when(owner.getLastName()).thenReturn("Franklin");
        when(owner.getAddress()).thenReturn("110 W. Liberty St.");
        when(owner.getCity()).thenReturn("Madison");
        when(owner.getTelephone()).thenReturn("6085551023");

        when(pet1.getId()).thenReturn(1);
        when(pet1.getName()).thenReturn("Max");
        when(pet1.getBirthDate()).thenReturn(PetMigration.convertToLocalDate(date));
        when(pet1.getTypeId()).thenReturn(1);
        when(pet1.getOwnerId()).thenReturn(1);


        when(pet2.getId()).thenReturn(2);
        when(pet2.getName()).thenReturn("Coco");
        when(pet2.getBirthDate()).thenReturn(PetMigration.convertToLocalDate(date));
        when(pet2.getTypeId()).thenReturn(2);
        when(pet2.getOwnerId()).thenReturn(2);


        when(pet3.getId()).thenReturn(3);
        when(pet3.getName()).thenReturn("Mr. turtle");
        when(pet3.getBirthDate()).thenReturn(PetMigration.convertToLocalDate(date));
        when(pet3.getTypeId()).thenReturn(3);
        when(pet3.getOwnerId()).thenReturn(3);

        when(pet4.getId()).thenReturn(4);
        when(pet4.getName()).thenReturn("Latte");
        when(pet4.getBirthDate()).thenReturn(PetMigration.convertToLocalDate(date));
        when(pet4.getTypeId()).thenReturn(4);
        when(pet4.getOwnerId()).thenReturn(4);

        when(pet5.getId()).thenReturn(5);
        when(pet5.getName()).thenReturn("Pumpkin");
        when(pet5.getBirthDate()).thenReturn(PetMigration.convertToLocalDate(date));
        when(pet5.getTypeId()).thenReturn(5);
        when(pet5.getOwnerId()).thenReturn(1);


        oldDataStorePets = new ArrayList<>();
        oldDataStorePets.add(pet1);
        oldDataStorePets.add(pet2);
        oldDataStorePets.add(pet3);


    }

    @Test()
    @Order(1)
    public void testForklift() {

        assertEquals(3, petMigration.forkliftTestOnly(oldDataStorePets));

    }

    @Test
    @Order(2)
    public void testCheckConsistency() {

        oldDataStorePets.add(pet4);

        assertEquals(1, petMigration.checkConsistenciesTestOnly(oldDataStorePets));
    }

    @Test
    @Order(3)
    public void testShadowReadConsistencyChecker() {

        oldDataStorePets.add(pet5);


        petMigration.shadowWriteToNewDatastore(pet5);


        assertTrue(petMigration.shadowReadWriteConsistencyChecker(pet5));


    }

    @AfterAll
    public static void closeConnection() throws SQLException {
        petMigration.closeConnections();
    }

}

