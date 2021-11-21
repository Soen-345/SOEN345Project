package org.springframework.samples.petclinic.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledCheck {

    private static final Logger log = LoggerFactory.getLogger(ScheduledCheck.class);
    private final VetMigration vetMigration = new VetMigration();
    private final VisitMigration visitMigration = new VisitMigration();

    // every 20 seconds
    @Scheduled(cron = "0/20 * * * * ?")
    public void consistencyCheck() {
        vetMigration.checkConsistencies();
        visitMigration.checkConsistencies();

    }
}
