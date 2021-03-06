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
package org.springframework.samples.petclinic.vet;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.util.SerializationUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dave Syer
 */
class VetTests {

	@Test
	void testSerialization() {
		Vet vet = new Vet(12,"Zaphod", "Beeblebrox");
		Vet other = (Vet) SerializationUtils.deserialize(SerializationUtils.serialize(vet));
		assertThat(other.getFirstName()).isEqualTo(vet.getFirstName());
		assertThat(other.getLastName()).isEqualTo(vet.getLastName());
		assertThat(other.getId()).isEqualTo(vet.getId());
	}

	// getSpecialties, getNrOfSpecialties and addSpecialty methods are missing for testing
	@Test
	public void testGetSpecialties() {

		Vet vet = new Vet();
		Specialty specialty = Mockito.mock(Specialty.class);
		Specialty specialty2 = Mockito.mock(Specialty.class);

		when(specialty.getName()).thenReturn("radiology");
		when(specialty2.getName()).thenReturn("surgery");
		vet.addSpecialty(specialty);
		vet.addSpecialty(specialty2);
		assertEquals(specialty, vet.getSpecialties().get(0));
		assertEquals(specialty2, vet.getSpecialties().get(1));

	}

	@Test
	public void testAddSpecialty() {
		Vet vet = new Vet();

		Specialty specialty = Mockito.mock(Specialty.class);
		assertEquals(0, vet.getNrOfSpecialties()); // addSpeciality not used so should
													// return 0
		vet.addSpecialty(specialty); // should increment number of specialities by 1
		assertEquals(1, vet.getNrOfSpecialties());
	}

	@Test
	public void testGetNrOfSpecialties() {
		Vet vet = new Vet();

		Specialty specialty = Mockito.mock(Specialty.class); // specialty number 1
		Specialty specialty2 = Mockito.mock(Specialty.class); // specialty number 2
		vet.addSpecialty(specialty);
		vet.addSpecialty(specialty2);
		assertEquals(2, vet.getNrOfSpecialties());

	}

}
