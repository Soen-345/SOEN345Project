package org.springframework.samples.petclinic.migration;

import org.springframework.samples.petclinic.vet.Specialty;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SpecialtiesDAO {
    private Connection SQLite_CONNECTION;
    private Connection H2_CONNECTION;

    public SpecialtiesDAO() {
        SQLite_CONNECTION = DatastoreConnection.connectSqlite();
        H2_CONNECTION = DatastoreConnection.connectH2();
    }

    protected void initTable() {
        String query = "DROP TABLE IF EXISTS specialties;";
        try {
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        this.createSpecialtyTable();
    }

    protected void createSpecialtyTable() {

        String createQuery =
                "CREATE TABLE IF NOT EXISTS specialties (\n" +
                        "                      id         INTEGER IDENTITY PRIMARY KEY,\n" +
                        "                      name  VARCHAR(80)\n" +
                        ");";
        try {
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(createQuery);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    protected Specialty getSpecialty(Integer specialtyId, Datastores datastore) {
        Specialty specialty = null;
        String query = "SELECT id, name FROM specialties WHERE id = " + specialtyId + ";";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                specialty = new Specialty(resultSet.getString("name"),
                        resultSet.getInt("id"));
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        if (datastore == Datastores.H2) {
            try {
                Statement statement = H2_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                specialty = new Specialty(resultSet.getString("name"),
                        resultSet.getInt("id"));
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return specialty;
    }

    protected Map<Integer, Specialty> getAllSpecialties(Datastores datastore) {
        Map<Integer, Specialty> specialities = new HashMap<>();
        String query = "SELECT * FROM specialties;";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    specialities.put(resultSet.getInt("id"),
                            new Specialty(resultSet.getString("name"),
                                    resultSet.getInt("id")));
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        if (datastore == Datastores.H2) {
            try {
                Statement statement = H2_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    specialities.put(resultSet.getInt("id"),
                            new Specialty(resultSet.getString("name"),
                                    resultSet.getInt("id")));
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        return specialities;
    }

    protected boolean addSpecialty(Specialty specialty, Datastores datastore) {
        String insertQuery = "INSERT INTO specialties (id, name) VALUES (" + specialty.getId()
                + ",'" + specialty.getName() + "');";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                statement.execute(insertQuery);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
        if (datastore == Datastores.H2) {
            try {
                Statement statement = H2_CONNECTION.createStatement();
                statement.execute(insertQuery);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
        return true;
    }

    protected void update(Specialty specialty, Datastores datastore) {
        String query = "UPDATE specialties SET name = '" + specialty.getName()
                + "' WHERE id = " + specialty.getId() + ";";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                statement.execute(query);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        if (datastore == Datastores.H2) {
            try {
                Statement statement = H2_CONNECTION.createStatement();
                statement.execute(query);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

    }

    protected void closeConnections() throws SQLException {
        SQLite_CONNECTION.close();
        H2_CONNECTION.close();
    }

}
