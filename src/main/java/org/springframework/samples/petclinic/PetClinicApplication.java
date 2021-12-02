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

package org.springframework.samples.petclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.samples.petclinic.migration.Datastores;
import org.springframework.samples.petclinic.migration.MigrationToggles;
import org.springframework.samples.petclinic.migration.OwnerMigration;
import org.springframework.samples.petclinic.migration.VetMigration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;

/**
 * PetClinic Spring Boot Application.
 *
 * @author Dave Syer
 *
 */
@EnableScheduling
@SpringBootApplication(proxyBeanMethods = false)
public class PetClinicApplication {

	public static void main(String[] args) {

		MigrationToggles.isUnderTest = false;
		MigrationToggles.isSQLiteEnabled = true;
		MigrationToggles.isH2Enabled = true;
		MigrationToggles.isShadowReadEnabled = false;
		SpringApplication.run(PetClinicApplication.class, args);

	}

}
