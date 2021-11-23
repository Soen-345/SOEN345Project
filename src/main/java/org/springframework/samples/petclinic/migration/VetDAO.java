package org.springframework.samples.petclinic.migration;

import org.springframework.samples.petclinic.vet.Vet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sevag Eordkian
 */
public class VetDAO {

    private Connection SQLite_CONNECTION;
    private Connection H2_CONNECTION;

    public VetDAO() {
        SQLite_CONNECTION = DatastoreConnection.connectSqlite();
        H2_CONNECTION = DatastoreConnection.connectH2();
    }

    protected void initTable() {
        String query = "DROP TABLE IF EXISTS vets;";
        try {
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        this.createVetTable();
    }

    protected void createVetTable() {

        String createQuery =
                "CREATE TABLE IF NOT EXISTS vets (\n" +
                "                      id         INTEGER IDENTITY PRIMARY KEY,\n" +
                "                      first_name VARCHAR(30),\n" +
                "                      last_name  VARCHAR(30)\n" +
                ");";
        String indexQuery = "CREATE INDEX vets_last_name ON vets (last_name);";
        try {
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(createQuery);
            Statement statement1 = SQLite_CONNECTION.createStatement();
            statement1.execute(indexQuery);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    protected Vet getVet(Integer vetId, Datastores datastore) {
        Vet vet = null;
        String query = "SELECT id, first_name, last_name FROM vets WHERE id = " + vetId + ";";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                vet = new Vet(resultSet.getInt("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"));
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        if (datastore == Datastores.H2) {
            try {
                Statement statement = H2_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                vet = new Vet(resultSet.getInt("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"));
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return vet;
    }

    protected Map<Integer, Vet> getAllVets(Datastores datastore) {
        Map<Integer, Vet> vets = new HashMap<>();
        String query = "SELECT * FROM vets;";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    vets.put(resultSet.getInt("id"),
                            new Vet(resultSet.getInt("id"),
                                    resultSet.getString("first_name"),
                                    resultSet.getString("last_name")));
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
                    vets.put(resultSet.getInt("id"),
                            new Vet(resultSet.getInt("id"),
                                    resultSet.getString("first_name"),
                                    resultSet.getString("last_name")));
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        return vets;
    }

    protected boolean addVet(Vet vet, Datastores datastore) {
        String insertQuery = "INSERT INTO vets (id, first_name, last_name) VALUES (" + vet.getId()
                + ",'" + vet.getFirstName() + "','" + vet.getLastName() + "');";
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

    protected void update(Vet vet, Datastores datastore) {
        String query = "UPDATE vets SET first_name = '" + vet.getFirstName()
                + "', last_name = '" + vet.getLastName() + "' WHERE id = " + vet.getId() + ";";
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
