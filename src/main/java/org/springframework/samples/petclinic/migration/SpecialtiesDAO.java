package org.springframework.samples.petclinic.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.vet.Specialty;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpecialtiesDAO implements IDAO<Specialty> {

    private static final Logger log = LoggerFactory.getLogger(SpecialtiesDAO.class);

    private Connection SQLite_CONNECTION;
    private Connection H2_CONNECTION;

    public SpecialtiesDAO() {
        SQLite_CONNECTION = DatastoreConnection.connectSqlite();
        H2_CONNECTION = DatastoreConnection.connectH2();
    }

    public void initTable() {
        String query = "DROP TABLE IF EXISTS specialties;";
        try {
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        this.createSpecialtyTable();
    }

    private void createSpecialtyTable() {
        String createQuery =
                "CREATE TABLE IF NOT EXISTS specialties (\n" +
                        "                      id         INTEGER IDENTITY PRIMARY KEY,\n" +
                        "                      name  VARCHAR(80)\n" +
                        ");";
        String indexQuery = "CREATE INDEX specialties_name ON specialties (name);";
        try {
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(createQuery);
            Statement statement1 = SQLite_CONNECTION.createStatement();
            statement1.execute(indexQuery);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    public Specialty get(Integer specialtyId, Datastores datastore) {
        Specialty specialty = null;
        String query = "SELECT id, name FROM specialties WHERE id = " + specialtyId + ";";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                specialty = new Specialty(resultSet.getString("name"),
                        resultSet.getInt("id"));
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        if (datastore == Datastores.H2) {
            try {
                Statement statement = H2_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                specialty = new Specialty(resultSet.getString("name"),
                        resultSet.getInt("id"));
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return specialty;
    }

    public List<Specialty> getAll(Datastores datastore) {
        List<Specialty> specialities = new ArrayList<>();
        String query = "SELECT * FROM specialties;";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    specialities.add(new Specialty(resultSet.getString("name"),
                            resultSet.getInt("id")));
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        if (datastore == Datastores.H2) {
            try {
                Statement statement = H2_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    specialities.add(new Specialty(resultSet.getString("name"),
                            resultSet.getInt("id")));
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }

        return specialities;
    }

    public boolean migrate(Specialty specialty) {
        String insertQuery = "INSERT INTO specialties (id, name) VALUES (" + specialty.getId()
                + ",'" + specialty.getName() + "');";
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                statement.execute(insertQuery);
            } catch (SQLException e) {
                log.error(e.getMessage());
                return false;
            }
        return true;
    }

    public void add(Specialty specialty, Datastores datastore) {
        String insertQuery = "INSERT INTO specialties (id, name) VALUES (" + specialty.getId()
                + ",'" + specialty.getName() + "');";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                statement.execute(insertQuery);
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        if (datastore == Datastores.H2) {
            try {
                Statement statement = H2_CONNECTION.createStatement();
                statement.execute(insertQuery);
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
    }

    public void update(Specialty specialty, Datastores datastore) {
        String query = "UPDATE specialties SET name = '" + specialty.getName()
                + "' WHERE id = " + specialty.getId() + ";";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                statement.execute(query);
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        if (datastore == Datastores.H2) {
            try {
                Statement statement = H2_CONNECTION.createStatement();
                statement.execute(query);
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }

    }

    public void closeConnections() throws SQLException {
        SQLite_CONNECTION.close();
        H2_CONNECTION.close();
    }

}
