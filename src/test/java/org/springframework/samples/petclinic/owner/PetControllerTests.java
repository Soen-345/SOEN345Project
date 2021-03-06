/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.owner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.petclinic.migration.OwnerMigration;
import org.springframework.samples.petclinic.migration.PetMigration;
import org.springframework.samples.petclinic.migration.TypeMigration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


/**
 * Test class for the {@link PetController}
 *
 * @author Colin But
 */
@WebMvcTest(value = PetController.class,
		includeFilters = @ComponentScan.Filter(value = PetTypeFormatter.class, type = FilterType.ASSIGNABLE_TYPE))
class PetControllerTests {

	private static final int TEST_OWNER_ID = 1;

	private static final int TEST_PET_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PetRepository pets;

	@MockBean
	private OwnerRepository owners;

	@MockBean
	private OwnerMigration ownerMigration;

	@MockBean
	private PetMigration petMigration;

	@MockBean
	private TypeMigration typeMigration;

	@Mock
	private PetType hamster;

	@Mock
	private Owner betty;

	@Mock
	private Pet max;

	@BeforeEach
	void setup() {

		when(betty.getId()).thenReturn(TEST_OWNER_ID);
		when(betty.getFirstName()).thenReturn("Betty");
		when(betty.getLastName()).thenReturn("Davis");
		when(betty.getAddress()).thenReturn("638 Cardinal Ave.");
		when(betty.getCity()).thenReturn("Sun Prairie");
		when(betty.getTelephone()).thenReturn("6085551749");

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String date = "2015-02-12";
		LocalDate localDate = LocalDate.parse(date, formatter);

		when(max.getId()).thenReturn(TEST_PET_ID);
		when(max.getName()).thenReturn("max");
		when(max.getOwner()).thenReturn(betty);
		when(max.getType()).thenReturn(hamster);
		when(max.getBirthDate()).thenReturn(localDate);
		when(hamster.getId()).thenReturn(1);
		when(hamster.getName()).thenReturn("hamster");
		when(betty.getPet("max")).thenReturn(max);
		/*
		given(this.pets.findPetTypes()).willReturn(Lists.newArrayList(hamster));
		given(this.owners.findById(TEST_OWNER_ID)).willReturn(betty);
		given(this.pets.findById(TEST_PET_ID)).willReturn(max);
		*/
		when(this.typeMigration.findTypes()).thenReturn(Lists.newArrayList(hamster));
		when(this.ownerMigration.shadowRead(TEST_OWNER_ID)).thenReturn(betty);
		when(this.petMigration.shadowRead(TEST_PET_ID)).thenReturn(max);
		when(this.petMigration.shadowReadWriteConsistencyChecker(any(Pet.class))).thenReturn(true);

	}

	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/new", TEST_OWNER_ID)).andExpect(status().isOk())
				.andExpect(view().name("pets/createOrUpdatePetForm")).andExpect(model().attributeExists("pet"));
	}

	@Test
	void testProcessCreationFormSuccess() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).param("name", "Betty")
				.param("type", "hamster").param("birthDate", "2015-02-12")).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void testProcessCreationFormHasErrors() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).param("name", "Betty").param("birthDate",
				"2015-02-12")).andExpect(model().attributeHasNoErrors("owner"))
				.andExpect(model().attributeHasErrors("pet")).andExpect(model().attributeHasFieldErrors("pet", "type"))
				.andExpect(model().attributeHasFieldErrorCode("pet", "type", "required")).andExpect(status().isOk())
				.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testInitUpdateForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID))
				.andExpect(status().isOk()).andExpect(model().attributeExists("pet"))
				.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessUpdateFormSuccess() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID).param("name", "Betty")
				.param("type", "hamster").param("birthDate", "2015-02-12")).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void testProcessUpdateFormHasErrors() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID).param("name", "Betty")
				.param("birthDate", "2015/02/12")).andExpect(model().attributeHasNoErrors("owner"))
				.andExpect(model().attributeHasErrors("pet")).andExpect(status().isOk())
				.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

}
