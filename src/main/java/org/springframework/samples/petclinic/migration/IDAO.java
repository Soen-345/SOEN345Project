package org.springframework.samples.petclinic.migration;

import java.sql.SQLException;
import java.util.List;

public interface IDAO<T> {

    void initTable();

    List<T> getAll(Datastores datastore);

    boolean migrate(T t);

    void update(T t, Datastores datastore);

    T get(Integer id, Datastores datastore);

    void closeConnections() throws SQLException;


}
