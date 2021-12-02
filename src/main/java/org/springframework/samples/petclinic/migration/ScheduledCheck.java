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

    private int totalInconsistencies = 0;
    private int numRuns = 0;

    @PostConstruct
    private void forklift() {

        if (MigrationToggles.isSQLiteEnabled && MigrationToggles.isH2Enabled) {

            log.info("**** FORKLIFT STARTING ****");

            vetMigration.forklift();
            specialtiesMigration.forklift();
            vetSpecialtiesMigration.forklift();
            visitMigration.forklift();
            ownerMigration.forklift();
            typeMigration.forklift();
            petMigration.forklift();

            log.info("FORKLIFT COMPLETED");
        }
    }


    // every 30 seconds
    @Async
    @Scheduled(fixedDelay = 30000)
    public void consistencyCheck() {

        if (MigrationToggles.isSQLiteEnabled && MigrationToggles.isH2Enabled) {
            this.numRuns++;

            log.info("**** CONSISTENCY CHECKING STARTING ****");

            int visitCons = visitMigration.checkConsistencies();
            int ownerCons = ownerMigration.checkConsistencies();
            int vetCons = vetMigration.checkConsistencies();
            int specialtyCons = specialtiesMigration.checkConsistencies();
            int vetSpecialtyCons = vetSpecialtiesMigration.checkConsistencies();
            int typeCons = typeMigration.checkConsistencies();
            int petCons = petMigration.checkConsistencies();

            this.totalInconsistencies = vetCons + visitCons + petCons + typeCons + vetSpecialtyCons + ownerCons + specialtyCons;

            log.info("OWNER TABLE: " + ownerCons);
            log.info("PET TABLE: " + petCons);
            log.info("VISIT TABLE: " + visitCons);
            log.info("VET TABLE: " + vetCons);
            log.info("SPECIALTY TABLE: " + specialtyCons);
            log.info("VET SPECIALTY TABLE: " + vetSpecialtyCons);
            log.info("PET TYPES TABLE: " + typeCons);

            log.info("**** CONSISTENCY CHECKING DONE ****");

            if (this.numRuns > 5 && totalInconsistencies < 3) {
                MigrationToggles.isH2Enabled = false;
                MigrationToggles.isShadowReadEnabled = true;
                ownerMigration.updateData();
                MigrationToggles.consistencyHashChecking = true;
                log.info("**** CONGRATS! YOU'VE MIGRATED FROM H2 TO SQLITE SUCCESSFULLY ****");
            }
        }
    }

    @Async
    @Scheduled(fixedDelay = 30000)
    public void hashConsistencyChecking(){
        if (MigrationToggles.consistencyHashChecking){
            if (!ownerMigration.hashConsistencyChecker()){
                log.warn("owner data corrupted");
            }else{
                log.info("no corruption");
            }
        }
    }
}

