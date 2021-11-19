package org.springframework.samples.petclinic.migration;

import java.util.List;

public class OwnerMigration implements IMigration{

    @Override
    public void forklift(List owners) {

    }

    @Override
    public int checkConsistencies() {
        return 0;
    }

    @Override
    public void logInconsistency() {

    }
}
