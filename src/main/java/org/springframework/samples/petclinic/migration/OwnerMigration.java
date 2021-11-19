package org.springframework.samples.petclinic.migration;

public class OwnerMigration implements IMigration{
    @Override
    public void forklift() {

    }

    @Override
    public int checkConsistencies() {
        return 0;
    }

    @Override
    public void logInconsistency() {

    }
}
