package org.springframework.samples.petclinic.vet;

import org.junit.jupiter.api.Test;

class VetsTests {

	@Test

	void VetsTest() {

		Vets vets = new Vets();
		vets.getVetList();
	}

}
