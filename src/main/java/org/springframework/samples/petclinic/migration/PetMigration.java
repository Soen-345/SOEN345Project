package org.springframework.samples.petclinic.migration;

public class PetMigration implements IMigration{
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
