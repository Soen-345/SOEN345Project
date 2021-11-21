package org.springframework.samples.petclinic.migration;

import org.springframework.samples.petclinic.visit.Visit;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sevag Eordkian
 */
public class VisitDAO {

    private Connection SQLite_CONNECTION;
    private Connection H2_CONNECTION;

    public VisitDAO() {
        SQLite_CONNECTION = DatastoreConnection.connectSqlite();
        H2_CONNECTION = DatastoreConnection.connectH2();
    }

    protected void initTable() {
        String query = "DROP TABLE IF EXISTS visits;";
        try {
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        this.createVisitTable();
    }

    protected void createVisitTable() {

        String createQuery =
                "CREATE TABLE IF NOT EXISTS visits (\n" +
                        "                        id          INTEGER IDENTITY PRIMARY KEY,\n" +
                        "                        pet_id      INTEGER NOT NULL,\n" +
                        "                        visit_date  DATE,\n" +
                        "                        description VARCHAR(255)\n" +
                        ");";
        try {
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(createQuery);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    protected Map<Integer, Visit> getAllVisits(Datastores datastore) {
        Map<Integer, Visit> visits = new HashMap<>();
        String query = "SELECT * FROM visits;";

        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    visits.put(resultSet.getInt("id"),
                            new Visit(resultSet.getInt("id"),
                                    resultSet.getInt("pet_id"),
                                    VisitMigration.convertToLocalDateViaInstant
                                            (new SimpleDateFormat("yyyy-MM-dd")
                                                    .parse(resultSet.getString("visit_date"))),
                                    resultSet.getString("description")));
                }
            } catch (SQLException | ParseException e) {
                System.out.println(e.getMessage());
            }
        }
        if (datastore == Datastores.H2) {
            try {
                Statement statement = H2_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    visits.put(resultSet.getInt("id"),
                            new Visit(resultSet.getInt("id"),
                                    resultSet.getInt("pet_id"),
                                    VisitMigration.convertToLocalDateViaInstant
                                            (new SimpleDateFormat("yyyy-MM-dd")
                                                    .parse(resultSet.getString("visit_date"))),
                                    resultSet.getString("description")));
                }
            } catch (SQLException | ParseException e) {
                System.out.println(e.getMessage());
            }
        }
        return visits;
    }

    protected boolean addVisit(Visit visit, Datastores datastore) {
        String insertQuery = "INSERT INTO visits (id, pet_id, visit_date, description) " +
                "VALUES (" + visit.getId() + "," + visit.getPetId() + ",'" +
                java.sql.Date.valueOf(visit.getDate()) +
                "', '" + visit.getDescription()  +  "');";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                statement.execute(insertQuery);
            }
            catch (SQLException e) {
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

    protected void update(Visit visit, Datastores datastore) {
        String query = "UPDATE visits SET pet_id = " + visit.getPetId()
                + ", visit_date = " + Date.valueOf(visit.getDate())
                +", description = '" + visit.getDescription() + "' WHERE id = " + visit.getId() + ";";
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

    protected Visit getVisit(Integer visitId, Datastores datastore) {
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
                System.out.println(e.getMessage());
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
                System.out.println(e.getMessage());
            }
        }
        return visit;
    }

    protected void closeConnections() throws SQLException {
        SQLite_CONNECTION.close();
        H2_CONNECTION.close();
    }


}
