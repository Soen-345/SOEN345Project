package org.springframework.samples.petclinic.migration;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class PetMigrationTest {
    private static PetMigration petMigration;

    private Map<Integer, Pet> oldDataStorePets;

    @Mock
    Pet pet1;
    Owner beshoy;
    PetType hamster;
    @Mock
    Pet pet2;
    Owner sevag;
    PetType puppy;
    @Mock
    Pet pet3;
    Owner ali;
    PetType bird;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        MigrationToggles.isUnderTest = true;

        petMigration = new PetMigration();

        when(pet1.getId()).thenReturn(1);
        when(pet1.getName()).thenReturn("pet1");
        when(pet1.getBirthDate()).thenReturn(LocalDate.parse("2020-11-08"));
        when(pet1.getType()).thenReturn((PetType) when(hamster.getId()).thenReturn(1));
        when(pet1.getOwner()).thenReturn((Owner) when(beshoy.getId()).thenReturn(1));


        when(pet2.getId()).thenReturn(2);
        when(pet2.getName()).thenReturn("pet2");
        when(pet2.getBirthDate()).thenReturn(LocalDate.parse("2021-07-28"));
        when(pet2.getType()).thenReturn((PetType) when(puppy.getId()).thenReturn(2));
        when(pet2.getOwner()).thenReturn((Owner) when(sevag.getId()).thenReturn(2));


        when(pet3.getId()).thenReturn(1);
        when(pet3.getName()).thenReturn("pet3");
        when(pet3.getBirthDate()).thenReturn(LocalDate.parse("2019-05-18"));
        when(pet3.getType()).thenReturn((PetType) when(bird.getId()).thenReturn(3));
        when(pet3.getOwner()).thenReturn((Owner) when(ali.getId()).thenReturn(3));


        oldDataStorePets = new HashMap<>();
        oldDataStorePets.put(pet1.getId(), pet1);
        oldDataStorePets.put(pet2.getId(), pet2);
        oldDataStorePets.put(pet3.getId(), pet3);

    }
    @Test()
    @Order(1)
    public void testForklift() {

        assertEquals(3, petMigration.forklift(oldDataStorePets));

    }

    @Test
    @Order(2)
    public void testCheckConsistency() {

        oldDataStorePets.put(pet3.getId(), pet3);


        assertEquals(1, petMigration.checkConsistencies(oldDataStorePets));
    }

    @Test
    @Order(3)
    public void testShadowReadConsistencyChecker() {

        oldDataStorePets.put(pet2.getId(), pet2);


        petMigration.shadowWrite(pet2);


        assertTrue(petMigration.shadowReadConsistencyChecker(pet2));


    }

    @AfterAll
    public static void closeConnection() throws SQLException {
        petMigration.closeConnections();
    }

}

