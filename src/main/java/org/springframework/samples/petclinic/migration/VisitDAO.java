package org.springframework.samples.petclinic.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.visit.Visit;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sevag Eordkian
 */
public class VisitDAO implements IDAO<Visit>{

    private static final Logger log = LoggerFactory.getLogger(VisitDAO.class);

    private Connection SQLite_CONNECTION;
    private Connection H2_CONNECTION;

    public VisitDAO() {
        SQLite_CONNECTION = DatastoreConnection.connectSqlite();
        H2_CONNECTION = DatastoreConnection.connectH2();
    }

    public void initTable() {
        String query = "DROP TABLE IF EXISTS visits;";
        try {
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        this.createVisitTable();
    }

    private void createVisitTable() {
        String createQuery =
                "CREATE TABLE IF NOT EXISTS visits (\n" +
                        "                        id          INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "                        pet_id      INTEGER NOT NULL,\n" +
                        "                        visit_date  DATE,\n" +
                        "                        description VARCHAR(255), \n" +
                        "                       FOREIGN KEY (pet_id) REFERENCES pets (id)" +
                        ");";
        String indexQuery = "CREATE INDEX visits_pet_id ON visits (pet_id);";
        try {
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(createQuery);
            Statement statement2 = SQLite_CONNECTION.createStatement();
            statement2.execute(indexQuery);

        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    public List<Visit> getAll(Datastores datastore) {
        List<Visit> visits = new ArrayList<>();
        String query = "SELECT * FROM visits;";

        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    visits.add(new Visit(resultSet.getInt("id"),
                            resultSet.getInt("pet_id"),
                            VisitMigration.convertToLocalDateViaInstant
                                    (new SimpleDateFormat("yyyy-MM-dd")
                                            .parse(resultSet.getString("visit_date"))),
                            resultSet.getString("description")));
                }
            } catch (SQLException | ParseException e) {
                log.error(e.getMessage());
            }
        }
        if (datastore == Datastores.H2) {
            try {
                Statement statement = H2_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    visits.add(new Visit(resultSet.getInt("id"),
                            resultSet.getInt("pet_id"),
                            VisitMigration.convertToLocalDateViaInstant
                                    (new SimpleDateFormat("yyyy-MM-dd")
                                            .parse(resultSet.getString("visit_date"))),
                            resultSet.getString("description")));
                }
            } catch (SQLException | ParseException e) {
                log.error(e.getMessage());
            }
        }
        return visits;
    }

    public boolean migrate(Visit visit) {
        String insertQuery = "INSERT INTO visits (id, pet_id, visit_date, description) " +
                "VALUES (" + visit.getId() + "," + visit.getPetId() + ",'" +
                java.sql.Date.valueOf(visit.getDate()) +
                "', '" + visit.getDescription()  +  "');";
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                statement.execute(insertQuery);
            }
            catch (SQLException e) {
                log.error(e.getMessage());
                return false;
            }
        return true;
    }

    public void add(Visit visit, Datastores datastore) {
        String insertQuery = "INSERT INTO visits (id, pet_id, visit_date, description) " +
                "VALUES (NULL" + "," + visit.getPetId() + ",'" +
                java.sql.Date.valueOf(visit.getDate()) +
                "', '" + visit.getDescription()  +  "');";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                statement.execute(insertQuery);
            }
            catch (SQLException e) {
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

    public void update(Visit visit, Datastores datastore) {
        String query = "UPDATE visits SET pet_id = " + visit.getPetId()
                + ", visit_date = " + Date.valueOf(visit.getDate())
                +", description = '" + visit.getDescription() + "' WHERE id = " + visit.getId() + ";";
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

    public Visit get(Integer visitId, Datastores datastore) {
        Visit visit = null;
        String query = "SELECT id, pet_id, visit_date, description FROM visits WHERE id = " + visitId + ";";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                visit = new Visit(resultSet.getInt("id"),
                        resultSet.getInt("pet_id"),
                        VisitMigration.convertToLocalDateViaInstant
                                (new SimpleDateFormat("yyyy-MM-dd")
                                        .parse(resultSet.getString("visit_date"))),
                        resultSet.getString("description"));
            } catch (SQLException | ParseException e) {
                log.error(e.getMessage());
            }
        }
        if (datastore == Datastores.H2) {
            try {
                Statement statement = H2_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                visit = new Visit(resultSet.getInt("id"),
                        resultSet.getInt("pet_id"),
                        VisitMigration.convertToLocalDateViaInstant(resultSet.getDate("visit_date")),
                        resultSet.getString("description"));
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return visit;
    }

    public List<Visit> getByPetId(Integer petId, Datastores datastore) {
        List<Visit> visits = new ArrayList<>();
        String query = "SELECT id, pet_id, visit_date, description FROM visits WHERE pet_id = " + petId + ";";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                visits.add(new Visit(resultSet.getInt("id"),
                        resultSet.getInt("pet_id"),
                        VisitMigration.convertToLocalDateViaInstant
                                (new SimpleDateFormat("yyyy-MM-dd")
                                        .parse(resultSet.getString("visit_date"))),
                        resultSet.getString("description")));
            } catch (SQLException | ParseException e) {
                log.error(e.getMessage());
            }
        }
        return visits;
    }


    public void closeConnections() throws SQLException {
        SQLite_CONNECTION.close();
        H2_CONNECTION.close();
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

}
