package org.springframework.samples.petclinic.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ScheduledCheck {

    private static final Logger log = LoggerFactory.getLogger(ScheduledCheck.class);
    private final VetMigration vetMigration = new VetMigration();
    private final VisitMigration visitMigration = new VisitMigration();
    private final PetMigration petMigration = new PetMigration();
    private final OwnerMigration ownerMigration = new OwnerMigration();
    private final VetSpecialtiesMigration vetSpecialtiesMigration = new VetSpecialtiesMigration();
    private final SpecialtiesMigration specialtiesMigration = new SpecialtiesMigration();
    private final TypeMigration typeMigration = new TypeMigration();

    @PostConstruct
    private void forklift() {
        log.info("**** FORKLIFT STARTING ****");
        ownerMigration.forklift();
        petMigration.forklift();
        visitMigration.forklift();
        vetMigration.forklift();
        specialtiesMigration.forklift();
        vetSpecialtiesMigration.forklift();
        typeMigration.forklift();
        log.info("FORKLIFT COMPLETED");
    }

    // every 30 seconds
    @Async
    @Scheduled(fixedDelay = 30000)
    public void consistencyCheck() {

        log.info("**** CONSISTENCY CHECKING STARTING ****");

        int ownerCons = ownerMigration.checkConsistencies();
        int petCons = petMigration.checkConsistencies();
        int visitCons = visitMigration.checkConsistencies();
        int vetCons = vetMigration.checkConsistencies();
        int specialtyCons = specialtiesMigration.checkConsistencies();
        int vetSpecialtyCons = vetSpecialtiesMigration.checkConsistencies();
        int typeCons = typeMigration.checkConsistencies();

        log.info("**** CONSISTENCY CHECKING DONE ****");

        log.info("OWNER TABLE: " + ownerCons);
        log.info("PET TABLE: " + petCons);
        log.info("VISIT TABLE: " + visitCons);
        log.info("VET TABLE: " + vetCons);
        log.info("SPECIALTY TABLE: " + specialtyCons);
        log.info("VET SPECIALTY TABLE: " + vetSpecialtyCons);
        log.info("PET TYPES TABLE: " + typeCons);
    }
}
