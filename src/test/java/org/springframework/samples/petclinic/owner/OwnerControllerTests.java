

package org.springframework.samples.petclinic.owner;


import org.assertj.core.util.Lists;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.samples.petclinic.migration.OwnerMigration;
import org.springframework.samples.petclinic.migration.VisitMigration;
import org.springframework.samples.petclinic.visit.Visit;
import org.springframework.samples.petclinic.visit.VisitRepository;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;
import java.lang.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link OwnerController}
 *
 * @author Colin But
 */

@WebMvcTest(OwnerController.class)
class OwnerControllerTests {

    private static final int TEST_OWNER_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerRepository owners;

    @MockBean
    private VisitRepository visits;

    @MockBean
    private OwnerMigration ownerMigration;

    @MockBean
    private VisitMigration visitMigration;

    @Mock
    private Owner george;

    @Mock
    private Owner owner2;

    @Mock
    private Pet max;

    @Mock
    private PetType dog;

    @Mock
    private Visit visit;

    @BeforeEach
    void setup() {

        when(this.george.getId()).thenReturn(TEST_OWNER_ID);
        when(this.george.getFirstName()).thenReturn("George");
        when(this.george.getLastName()).thenReturn("Franklin");
        when(this.george.getAddress()).thenReturn("110 W. Liberty St.");
        when(this.george.getCity()).thenReturn("Madison");
        when(this.george.getTelephone()).thenReturn("6085551023");
        when(this.george.getPets()).thenReturn(Lists.newArrayList(max));

        when(owner2.getId()).thenReturn(2);
        when(owner2.getFirstName()).thenReturn("Betty");
        when(owner2.getLastName()).thenReturn("Davis");
        when(owner2.getAddress()).thenReturn("638 Cardinal Ave.");
        when(owner2.getCity()).thenReturn("Sun Prairie");
        when(owner2.getTelephone()).thenReturn("6085551749");


        when(this.visit.getDate()).thenReturn(LocalDate.now());
        when(this.dog.getName()).thenReturn("dog");
        when(this.max.getId()).thenReturn(1);
        when(this.max.getType()).thenReturn(dog);
        when(this.max.getName()).thenReturn("Max");
        when(this.max.getBirthDate()).thenReturn(LocalDate.now());
        when(this.max.getVisits()).thenReturn(Lists.newArrayList(visit));
        when(this.george.getPetsInternal()).thenReturn(Collections.singleton(max));

/*
        when(this.owners.findById(TEST_OWNER_ID)).thenReturn(george);
        given(this.owners.findByFirstName(george.getFirstName())).willReturn(Lists.newArrayList(george));
        given(this.owners.findByLastName(george.getLastName())).willReturn(Lists.newArrayList(george));
        when(this.owners.findByLastName("")).thenReturn(Lists.newArrayList(george, owner2));
        when(this.visits.findByPetId(max.getId())).thenReturn(Collections.singletonList(visit));
*/

        when(this.ownerMigration.shadowRead(TEST_OWNER_ID)).thenReturn(george);
        when(this.ownerMigration.shadowReadWriteConsistencyChecker(any(Owner.class))).thenReturn(true);
        when(this.ownerMigration.shadowReadByFirstName("George")).thenReturn(Lists.newArrayList(george));
        when(this.ownerMigration.shadowReadByLastName(george.getLastName())).thenReturn(Lists.newArrayList(george));
        when(this.ownerMigration.shadowReadByLastName("")).thenReturn(Lists.newArrayList(george, owner2));
        when(this.visitMigration.shadowReadByPetId(max.getId())).thenReturn(Collections.singletonList(visit));

    }

    @Test
    void testInitCreationForm() throws Exception {
        OwnerToggles.isAddOwnerButtonEnabled = true;
        mockMvc.perform(get("/owners/new")).andExpect(status().isOk()).andExpect(model().attributeExists("owner"))
                .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }

    @Test
    void testProcessCreationFormSuccess() throws Exception {
        OwnerToggles.isAddOwnerButtonEnabled = true;
        mockMvc.perform(post("/owners/new").param("firstName", "Joe").param("lastName", "Bloggs")
                        .param("address", "123 Caramel Street").param("city", "London").param("telephone", "01316761638"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testProcessCreationFormHasErrors() throws Exception {
        OwnerToggles.isAddOwnerButtonEnabled = true;
        mockMvc.perform(
                        post("/owners/new").param("firstName", "Joe").param("lastName", "Bloggs").param("city", "London"))
                .andExpect(status().isOk()).andExpect(model().attributeHasErrors("owner"))
                .andExpect(model().attributeHasFieldErrors("owner", "address"))
                .andExpect(model().attributeHasFieldErrors("owner", "telephone"))
                .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }

    @Test
    void testInitFindForm() throws Exception {
        mockMvc.perform(get("/owners/find")).andExpect(status().isOk()).andExpect(model().attributeExists("owner"))
                .andExpect(view().name("owners/findOwners"));
    }

    @Test
    void testProcessFindFormSuccess() throws Exception {
        OwnerToggles.isSearchFirstNameEnabled = false;
        OwnerToggles.isSearchLastNameEnabled = true;
        mockMvc.perform(get("/owners")).andExpect(status().isOk()).andExpect(view().name("owners/ownersList"));
    }

    @Test
    void testProcessFindFormByLastName() throws Exception {
        OwnerToggles.isSearchFirstNameEnabled = false;
        OwnerToggles.isSearchLastNameEnabled = true;
        mockMvc.perform(get("/owners").param("lastName", "Franklin")).andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
    }

    @Test
    void testProcessFindFormByFirstName() throws Exception {
        OwnerToggles.isSearchFirstNameEnabled = true;
        OwnerToggles.isSearchLastNameEnabled = false;

        mockMvc.perform(get("/owners").param("firstName", "George")).andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
    }

    @Test
    void testProcessFindFormNoOwnersFound() throws Exception {
        mockMvc.perform(get("/owners").param("lastName", "Unknown Surname")).andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("owner", "lastName"))
                .andExpect(model().attributeHasFieldErrorCode("owner", "lastName", "notFound"))
                .andExpect(view().name("owners/findOwners"));
    }

    @Test
    void testInitUpdateOwnerForm() throws Exception {
        OwnerToggles.isUpdateOwnerEnabled = true;
        mockMvc.perform(get("/owners/{ownerId}/edit", TEST_OWNER_ID)).andExpect(status().isOk())
                .andExpect(model().attributeExists("owner"))
                .andExpect(model().attribute("owner", hasProperty("lastName", is("Franklin"))))
                .andExpect(model().attribute("owner", hasProperty("firstName", is("George"))))
                .andExpect(model().attribute("owner", hasProperty("address", is("110 W. Liberty St."))))
                .andExpect(model().attribute("owner", hasProperty("city", is("Madison"))))
                .andExpect(model().attribute("owner", hasProperty("telephone", is("6085551023"))))
                .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }

    @Test
    void testProcessUpdateOwnerFormSuccess() throws Exception {
        OwnerToggles.isUpdateOwnerEnabled = true;
        mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID).param("firstName", "Joe")
                        .param("lastName", "Bloggs").param("address", "123 Caramel Street").param("city", "London")
                        .param("telephone", "01616291589")).andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/{ownerId}"));
    }

    @Test
    void testProcessUpdateOwnerFormHasErrors() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID).param("firstName", "Joe")
                        .param("lastName", "Bloggs").param("city", "London")).andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("owner"))
                .andExpect(model().attributeHasFieldErrors("owner", "address"))
                .andExpect(model().attributeHasFieldErrors("owner", "telephone"))
                .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }

    @Test
    void testShowOwner() throws Exception {
        mockMvc.perform(get("/owners/{ownerId}", TEST_OWNER_ID)).andExpect(status().isOk())
                .andExpect(model().attribute("owner", hasProperty("lastName", is("Franklin"))))
                .andExpect(model().attribute("owner", hasProperty("firstName", is("George"))))
                .andExpect(model().attribute("owner", hasProperty("address", is("110 W. Liberty St."))))
                .andExpect(model().attribute("owner", hasProperty("city", is("Madison"))))
                .andExpect(model().attribute("owner", hasProperty("telephone", is("6085551023"))))
                .andExpect(model().attribute("owner", hasProperty("pets", not(empty()))))
                .andExpect(model().attribute("owner", hasProperty("pets", new BaseMatcher<List<Pet>>() {

                    @Override
                    public boolean matches(Object item) {
                        @SuppressWarnings("unchecked")
                        List<Pet> pets = (List<Pet>) item;
                        Pet pet = pets.get(0);
                        return !pet.getVisits().isEmpty();
                    }

                    @Override
                    public void describeTo(Description description) {
                        description.appendText("Max did not have any visits");
                    }
                }))).andExpect(view().name("owners/ownerDetails"));


    }

	@Test
	// tests the logger and rollback
	public void rollBackWithLoggerTest () throws Exception {
		//feature is dark --> no one can access the feature
		OwnerToggles.isSearchFirstNameEnabled=false;
		OwnerToggles.isSearchLastNameEnabled=true;

		//for when the feature is off
		//when(this.owners.findByLastName("")).thenReturn(Lists.newArrayList(george, new Owner()));
        when(this.ownerMigration.shadowReadByLastName("")).thenReturn(Lists.newArrayList(george, new Owner()));
		mockMvc.perform(get("/owners")).andExpect(status().isOk()).andExpect(view().name("owners/ownersList"));

		//when(this.owners.findByLastName(george.getLastName())).thenReturn(Lists.newArrayList(george));
        when(this.ownerMigration.shadowReadByLastName(george.getLastName())).thenReturn(Lists.newArrayList(george));
		mockMvc.perform(get("/owners").param("lastName", "Franklin")).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));

		//feature is on
		OwnerToggles.isSearchFirstNameEnabled=true;
		OwnerToggles.isSearchLastNameEnabled=false;

        when(this.ownerMigration.shadowReadByFirstName("")).thenReturn(Lists.newArrayList(george, new Owner()));
		mockMvc.perform(get("/owners")).andExpect(status().isOk()).andExpect(view().name("owners/ownersList"));

        when(this.ownerMigration.shadowReadByFirstName("George")).thenReturn(Lists.newArrayList(george));
		mockMvc.perform(get("/owners").param("firstName", "George")).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));

		//for when the feature is off --> rollback
		OwnerToggles.isSearchFirstNameEnabled=false;
		OwnerToggles.isSearchLastNameEnabled=true;

        when(this.ownerMigration.shadowReadByLastName("")).thenReturn(Lists.newArrayList(george, new Owner()));
		mockMvc.perform(get("/owners")).andExpect(status().isOk()).andExpect(view().name("owners/ownersList"));

        when(this.ownerMigration.shadowReadByLastName(george.getLastName())).thenReturn(Lists.newArrayList(george));
		mockMvc.perform(get("/owners").param("lastName", "Franklin")).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));

	}

	@Test
	void testRandomFeature() throws Exception{
		int interations = 100;

		given(this.owners.findByLastName("")).willReturn(Lists.newArrayList(george, new Owner()));
		given(this.owners.findByLastName(george.getLastName())).willReturn(Lists.newArrayList(george));
		given(this.owners.findByFirstName("")).willReturn(Lists.newArrayList(george, new Owner()));
		given(this.owners.findByFirstName(george.getFirstName())).willReturn(Lists.newArrayList(george));

		given(this.owners.findByLastName("NA")).willReturn(Lists.newArrayList());
		given(this.owners.findByFirstName("NA")).willReturn(Lists.newArrayList());

		for (int i = 0; i < interations; i++) {
			OwnerToggles.assignSearchNameFeature(30);
			// hypothesis: user find correct owner easier with first name
			// if search by last name user, it will take about 1-5 search to get the name right
			// if search by first name user enter correct name in 1-2 search

			int counter = 1;
			if (OwnerToggles.isSearchLastNameEnabled) {
				for (int j = 0; j < ThreadLocalRandom.current().nextDouble(0,5);j++) {

					mockMvc.perform(get("/owners").param("lastName", "NA")).andExpect(status().isOk())
							.andExpect(model().attributeHasFieldErrors("owner", "lastName"))
							.andExpect(model().attributeHasFieldErrorCode("owner", "lastName", "notFound"))
							.andExpect(view().name("owners/findOwners"));

					counter = counter + 1;
				}

				mockMvc.perform(get("/owners").param("lastName", "Franklin")).andExpect(status().is3xxRedirection())
						.andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));

				OwnerController.analytics.info("Number of searches to get it right for disabled: ," + counter);

			}

			if (OwnerToggles.isSearchFirstNameEnabled){
				for (int j = 0; j < ThreadLocalRandom.current().nextDouble(0,2);j++){

					mockMvc.perform(get("/owners").param("firstName", "NA")).andExpect(status().isOk())
							.andExpect(model().attributeHasFieldErrors("owner", "lastName"))
							.andExpect(model().attributeHasFieldErrorCode("owner", "lastName", "notFound"))
							.andExpect(view().name("owners/findOwners"));

					counter = counter + 1;

				}
				mockMvc.perform(get("/owners").param("firstName", "George")).andExpect(status().is3xxRedirection())
						.andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));


				OwnerController.analytics.info("Number of searches to get it right for enabled: ," + counter);

			}

		}

	}

}
