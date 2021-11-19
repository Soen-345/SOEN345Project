package org.springframework.samples.petclinic.migration;

import java.util.List;

public class PetMigration implements IMigration{

    @Override
    public void forklift(List pets) {

    }

    @Override
    public int checkConsistencies() {
        return 0;
    }

    @Override
    public void logInconsistency() {

    }
}
