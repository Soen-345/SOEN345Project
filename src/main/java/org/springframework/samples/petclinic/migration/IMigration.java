package org.springframework.samples.petclinic.migration;

import java.sql.SQLException;

public interface IMigration<T> {

    int forklift();

    int checkConsistencies();

    boolean shadowReadWriteConsistencyChecker(T t);

    void logInconsistency(T expected, T actual);

    void closeConnections() throws SQLException;

}
