package org.springframework.samples.petclinic.migration;

import java.util.List;

public interface IMigration {


    public void forklift(List list);

    public int checkConsistencies();

    public void logInconsistency();
}
