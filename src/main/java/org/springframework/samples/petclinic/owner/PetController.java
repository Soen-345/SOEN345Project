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

import org.springframework.samples.petclinic.migration.MigrationToggles;
import org.springframework.samples.petclinic.migration.OwnerMigration;
import org.springframework.samples.petclinic.migration.PetMigration;
import org.springframework.samples.petclinic.migration.TypeMigration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;


/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 */
@Controller
@RequestMapping("/owners/{ownerId}")
class PetController {

    private static final String VIEWS_PETS_CREATE_OR_UPDATE_FORM = "pets/createOrUpdatePetForm";

    private final PetRepository pets;
    private final PetMigration petMigration;
    private final TypeMigration typeMigration;
    private final OwnerMigration ownerMigration;


    private final OwnerRepository owners;

    public PetController(PetRepository pets, OwnerRepository owners, OwnerMigration ownerMigration,
                         TypeMigration typeMigration, PetMigration petMigration) {
        this.pets = pets;
        this.owners = owners;
        this.petMigration = petMigration;
        this.typeMigration = typeMigration;
        this.ownerMigration = ownerMigration;
    }

    @ModelAttribute("types")
    public Collection<PetType> populatePetTypes() {
        if (MigrationToggles.isH2Enabled) {
            return this.pets.findPetTypes();
        }
        if (MigrationToggles.isSQLiteEnabled) {
            return this.typeMigration.findTypes();
        }
        return null;
    }

    @ModelAttribute("owner")
    public Owner findOwner(@PathVariable("ownerId") int ownerId) {
        if (MigrationToggles.isH2Enabled) {
            return this.owners.findById(ownerId);
        }
        if (MigrationToggles.isSQLiteEnabled) {
            return this.ownerMigration.shadowRead(ownerId);
        }
        return null;
    }

    @InitBinder("owner")
    public void initOwnerBinder(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    @InitBinder("pet")
    public void initPetBinder(WebDataBinder dataBinder) {
        dataBinder.setValidator(new PetValidator());
    }

    @GetMapping("/pets/new")
    public String initCreationForm(Owner owner, ModelMap model) {
        Pet pet = new Pet();
        owner.addPet(pet);
        model.put("pet", pet);
        return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
    }

    @PostMapping("/pets/new")
    public String processCreationForm(Owner owner, @Valid Pet pet, BindingResult result, ModelMap model) {
        if (StringUtils.hasLength(pet.getName()) && pet.isNew() && owner.getPet(pet.getName(), true) != null) {
            result.rejectValue("name", "duplicate", "already exists");
        }
        owner.addPet(pet);
        if (result.hasErrors()) {
            model.put("pet", pet);
            return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
        } else {
            if (MigrationToggles.isH2Enabled) {
                this.pets.save(pet);
            }
            if (MigrationToggles.isSQLiteEnabled) {
                pet.setOwner(owner);
                this.petMigration.shadowWriteToNewDatastore(pet);
                this.petMigration.shadowReadWriteConsistencyChecker(pet);
            }
            return "redirect:/owners/{ownerId}";
        }
    }

    @GetMapping("/pets/{petId}/edit")
    public String initUpdateForm(@PathVariable("petId") int petId, ModelMap model) {
        Pet pet = null;
        if (MigrationToggles.isH2Enabled) {
            pet = this.pets.findById(petId);
        }
        if (MigrationToggles.isSQLiteEnabled && MigrationToggles.isShadowReadEnabled) {
            boolean success = this.petMigration.shadowReadWriteConsistencyChecker(this.petMigration.shadowRead(petId));
            if (success) {
                pet = this.petMigration.shadowRead(petId);
            }
        }
        model.put("pet", pet);
        return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
    }

    @PostMapping("/pets/{petId}/edit")
    public String processUpdateForm(@Valid Pet pet, BindingResult result, Owner owner, ModelMap model) {
        if (result.hasErrors()) {
            pet.setOwner(owner);
            model.put("pet", pet);
            return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
        } else {
            owner.addPet(pet);
            if (MigrationToggles.isH2Enabled) {
                this.pets.save(pet);
            }
            if (MigrationToggles.isSQLiteEnabled) {
           //     pet.setOwner(owner);
                this.petMigration.shadowUpdate(pet);
                this.petMigration.shadowReadWriteConsistencyChecker(pet);
            }
            return "redirect:/owners/{ownerId}";
        }
    }


}
