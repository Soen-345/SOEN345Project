package org.springframework.samples.petclinic.migration;

import java.sql.SQLException;
import java.util.Map;

public interface IDAO<T> {

    public void initTable();

    public Map<Integer, T> getAll(Datastores datastore);

    public boolean add(T t, Datastores datastore);

    public void update(T t, Datastores datastore);

    public T get(Integer id, Datastores datastore);

    public void closeConnections() throws SQLException;


}
