package org.springframework.samples.petclinic.migration;

import java.sql.SQLException;
import java.util.Map;

public interface IDAO<T> {

    void initTable();

    Map<Integer, T> getAll(Datastores datastore);

    boolean add(T t, Datastores datastore);

    void update(T t, Datastores datastore);

    T get(Integer id, Datastores datastore);

    void closeConnections() throws SQLException;


}
