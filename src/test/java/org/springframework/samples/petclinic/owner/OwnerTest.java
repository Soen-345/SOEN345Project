package org.springframework.samples.petclinic.owner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;
import org.springframework.samples.petclinic.model.Person;

import java.util.HashSet;

/**
 * @author Alireza Ziarizi
 */
public class OwnerTest {

	private Owner alireza;

	private Pet pet;

	private Pet existingPet;

	private HashSet<Pet> setOfPets;

	@BeforeEach
	public void setup() {
		alireza = new Owner("alireza", "Ziarizi", "1234 cdn", "Montreal", "5141111111");
		// mocking pet class
		pet = mock(Pet.class);
		existingPet = mock(Pet.class);

		setOfPets = new HashSet<>();

		when(pet.getName()).thenReturn("maxi");
		when(pet.isNew()).thenReturn(true);
		when(existingPet.getName()).thenReturn("charlie");
		when(existingPet.isNew()).thenReturn(false);
		setOfPets.add(pet);
		setOfPets.add(existingPet);
		alireza.setPetsInternal(setOfPets);

	}

	@Test
	public void testGetPetnew() {
		// testing without ignoreNew
		assertEquals(pet, alireza.getPet("maxi"));
		// testing with ignorenNew = false
		assertEquals(pet, alireza.getPet("maxi", false));
	}

	@Test
	public void testExistingPet() {
		assertEquals(existingPet, alireza.getPet("charlie"));
		assertEquals(existingPet, alireza.getPet("charlie", true));
	}

	@Test
	public void testfornopets() {
		assertEquals(setOfPets.size(), alireza.getPets().size());
	}

	@Test
	public void testPetNotFound() {
		assertEquals(null, alireza.getPet("haboub"));
	}

}
