package org.springframework.samples.petclinic.migration;

import java.sql.SQLException;

public interface IMigration<T> {

    public int forklift();

    public int checkConsistencies();

    public boolean shadowReadWriteConsistencyChecker(T t);

    public void logInconsistency(T expected, T actual);

    public void closeConnections() throws SQLException;
}
