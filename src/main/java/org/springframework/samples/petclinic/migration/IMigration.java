package org.springframework.samples.petclinic.migration;

public interface IMigration {


    public void forklift();

    public int checkConsistencies();

    public void logInconsistency();
}
