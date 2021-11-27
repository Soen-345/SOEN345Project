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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.samples.petclinic.migration.OwnerMigration;
import org.springframework.samples.petclinic.visit.VisitRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 */
@Controller
class OwnerController {

    private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";

    private final OwnerRepository owners;
    private final OwnerMigration ownerMigration;

    private VisitRepository visits;

    private static Logger logger = LogManager.getLogger("Analytics");



    public OwnerController(OwnerRepository clinicService, VisitRepository visits) {
        this.owners = clinicService;
        this.visits = visits;
        this.ownerMigration = new OwnerMigration();
    }

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    @GetMapping("/owners/new")
    public String initCreationForm(Map<String, Object> model) {
        if (OwnerToggles.isAddOwnerButtonEnabled) {
            Owner owner = new Owner();
            model.put("owner", owner);
            return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
        }
        return null;
    }

    @PostMapping("/owners/new")
    public String processCreationForm(@Valid Owner owner, BindingResult result) {

        if (OwnerToggles.isAddOwnerButtonEnabled) {
            if (result.hasErrors()) {
                return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
            } else {
                this.owners.save(owner);
                // shadow writing to new database
                this.ownerMigration.shadowWriteToNewDatastore(owner);
                this.ownerMigration.shadowReadWriteConsistencyChecker(owner);
                return "redirect:/owners/" + owner.getId();
            }
        }
        return "The Add Owner button is not enabled";
    }

    @GetMapping("/owners/find")
    public String initFindForm(Map<String, Object> model) {
        model.put("owner", new Owner());
        if(OwnerToggles.isSearchLastNameEnabled)
            model.put("nameType","Last");
        if(OwnerToggles.isSearchFirstNameEnabled)
            model.put("nameType","First");


        return "owners/findOwners";
    }

    @GetMapping("/owners")
    public String processFindForm(Owner owner, BindingResult result, Map<String, Object> model) {

        Collection<Owner> results=null;
        if(OwnerToggles.isSearchLastNameEnabled){
            // allow parameterless GET request for /owners to return all records
            if (owner.getLastName() == null) {
                owner.setLastName(""); // empty string signifies broadest possible search
            }

            // find owners by last name
            results = this.owners.findByLastName(owner.getLastName());

        }
        if (OwnerToggles.isSearchFirstNameEnabled) {
            // allow parameterless GET request for /owners to return all records
            if (owner.getFirstName() == null) {
                owner.setFirstName(""); // empty string signifies broadest possible search
            }

            // find owners by first name
            results = this.owners.findByFirstName(owner.getFirstName());
            logger.info("List of names\n" + results);

        }
        if(OwnerToggles.isSearchFirstNameEnabled||OwnerToggles.isSearchLastNameEnabled){
            if (results.isEmpty()) {
                // no owners found
                result.rejectValue("lastName", "notFound", "not found");
                return "owners/findOwners";
            } else if (results.size() == 1) {
                // 1 owner found
                owner = results.iterator().next();
                return "redirect:/owners/" + owner.getId();
            } else {
                // multiple owners found
                model.put("selections", results);
                return "owners/ownersList";
            }

        }
        result.rejectValue("lastName", "notFound", "not found");
        return "owners/findOwners";
    }

    @GetMapping("/owners/{ownerId}/edit")
    public String initUpdateOwnerForm(@PathVariable("ownerId") int ownerId, Model model) {
        if (OwnerToggles.isUpdateOwnerEnabled) {
            Owner owner = this.owners.findById(ownerId);
            model.addAttribute(owner);
            return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
        }
        return null;
    }

    @PostMapping("/owners/{ownerId}/edit")
    public String processUpdateOwnerForm(@Valid Owner owner, BindingResult result,
                                         @PathVariable("ownerId") int ownerId) {

        if (OwnerToggles.isUpdateOwnerEnabled) {
            if (result.hasErrors()) {
                return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
            } else {
                owner.setId(ownerId);
                this.owners.save(owner);
                // shadow writing to new database but updating
                this.ownerMigration.shadowWriteToNewDatastore(owner);
                this.ownerMigration.shadowReadWriteConsistencyChecker(owner);
                return "redirect:/owners/{ownerId}";
            }

        }
        return "Update owner feature not enabled";
    }

    /**
     * Custom handler for displaying an owner.
     *
     * @param ownerId the ID of the owner to display
     * @return a ModelMap with the model attributes for the view
     */
    @GetMapping("/owners/{ownerId}")
    public ModelAndView showOwner(@PathVariable("ownerId") int ownerId) {
        ModelAndView mav = new ModelAndView("owners/ownerDetails");
        Owner owner = this.owners.findById(ownerId);
        for (Pet pet : owner.getPets()) {
            pet.setVisitsInternal(visits.findByPetId(pet.getId()));
        }
        mav.addObject(owner);
        return mav;
    }

}
