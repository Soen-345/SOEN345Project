package org.springframework.samples.petclinic.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.vet.Specialty;
import org.springframework.samples.petclinic.vet.Vet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sevag Eordkian
 */
public class VetDAO implements IDAO<Vet>{

    private static final Logger log = LoggerFactory.getLogger(VetDAO.class);

    private Connection SQLite_CONNECTION;
    private Connection H2_CONNECTION;

    public VetDAO() {
        SQLite_CONNECTION = DatastoreConnection.connectSqlite();
        H2_CONNECTION = DatastoreConnection.connectH2();
    }

    public void initTable() {
        String query = "DROP TABLE IF EXISTS vets;";
        try {
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        this.createVetTable();
    }

    private void createVetTable() {
        String createQuery =
                "CREATE TABLE IF NOT EXISTS vets (\n" +
                "                      id         INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
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
            log.error(e.getMessage());
        }
    }

    public Vet get(Integer vetId, Datastores datastore) {
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
                log.error(e.getMessage());
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
                log.error(e.getMessage());
            }
        }
        return vet;
    }

    public List<Vet> getAll(Datastores datastore) {
        List<Vet> vets = new ArrayList<>();
        String query = "SELECT * FROM vets;";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    Specialty specialty;
                    int vet_id = resultSet.getInt("id");
                    Vet vet = new Vet(vet_id,
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name"));
                    Statement statement1 = SQLite_CONNECTION.createStatement();
                    ResultSet resultSet1 = statement1
                            .executeQuery("SELECT specialty_id FROM vet_specialties WHERE vet_id=" + vet_id + ";");
                    while (resultSet1.next()) {
                        int specialty_id = resultSet1.getInt("specialty_id");
                        Statement statement2 = SQLite_CONNECTION.createStatement();
                        ResultSet resultSet2 = statement2.executeQuery("SELECT name FROM specialties WHERE id = " + specialty_id + ";");
                        while (resultSet2.next()) {
                            specialty = new Specialty(resultSet2.getString("name"), specialty_id);
                            vet.addSpecialty(specialty);
                        }
                    }
                    vets.add(vet);
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
                    Specialty specialty;
                    int vet_id = resultSet.getInt("id");
                    Vet vet = new Vet(vet_id,
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name"));
                    Statement statement1 = H2_CONNECTION.createStatement();
                    ResultSet resultSet1 = statement1
                            .executeQuery("SELECT specialty_id FROM vet_specialties WHERE vet_id=" + vet_id + ";");
                    while (resultSet1.next()) {
                        int specialty_id = resultSet1.getInt("specialty_id");
                        Statement statement2 = H2_CONNECTION.createStatement();
                        ResultSet resultSet2 = statement2.executeQuery("SELECT name FROM specialties WHERE id = " + specialty_id + ";");
                        while (resultSet2.next()) {
                            specialty = new Specialty(resultSet2.getString("name"), specialty_id);
                            vet.addSpecialty(specialty);
                        }
                    }
                    vets.add(vet);
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }

        return vets;
    }

    public boolean migrate(Vet vet) {
        String insertQuery = "INSERT INTO vets (id, first_name, last_name) VALUES (" + vet.getId()
                + ",'" + vet.getFirstName() + "','" + vet.getLastName() + "');";
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                statement.executeUpdate(insertQuery);
            } catch (SQLException e) {
                log.error(e.getMessage());
                return false;
            }
        return true;
    }

    public void add(Vet vet, Datastores datastore) {
        String insertQuery = "INSERT INTO vets (id, first_name, last_name) VALUES (NULL"
                + ",'" + vet.getFirstName() + "','" + vet.getLastName() + "');";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                statement.executeUpdate(insertQuery);
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        if (datastore == Datastores.H2) {
            try {
                Statement statement = H2_CONNECTION.createStatement();
                statement.executeUpdate(insertQuery);
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
    }

    public void update(Vet vet, Datastores datastore) {
        String query = "UPDATE vets SET first_name = '" + vet.getFirstName()
                + "', last_name = '" + vet.getLastName() + "' WHERE id = " + vet.getId() + ";";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                statement.executeUpdate(query);
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        if (datastore == Datastores.H2) {
            try {
                Statement statement = H2_CONNECTION.createStatement();
                statement.executeUpdate(query);
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }

    }

    public void addHashStorage(String type,String hash){
        String insertQuery = "INSERT INTO hashTable (hashtype, hashStorage) VALUES (" +" '" + type + "'"+ ",'" + hash  + "');";
        try {
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(insertQuery);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }
    public String getHash(String type){
        String hashStorage  = "";
        String query = "SELECT hashStorage FROM hashTable WHERE hashtype = " +"'"+ type +"'" + ";";
        try {
            Statement statement = SQLite_CONNECTION.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            hashStorage = resultSet.getString("hashStorage");
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return hashStorage;
    }

    public void closeConnections() throws SQLException {
        SQLite_CONNECTION.close();
        H2_CONNECTION.close();
    }

}
