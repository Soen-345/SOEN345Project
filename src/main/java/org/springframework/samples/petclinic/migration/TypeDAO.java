package org.springframework.samples.petclinic.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.owner.PetType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeDAO implements IDAO<PetType>{

    private static final Logger log = LoggerFactory.getLogger(TypeDAO.class);

    private Connection SQLite_CONNECTION;
    private Connection H2_CONNECTION;

    public TypeDAO() {
        SQLite_CONNECTION = DatastoreConnection.connectSqlite();
        H2_CONNECTION = DatastoreConnection.connectH2();
    }

    public void initTable() {
        String query = "DROP TABLE IF EXISTS types;";
        try {
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        this.createTypeTable();
    }

    private void createTypeTable() {
        String createQuery =
                "CREATE TABLE IF NOT EXISTS types (\n" +
                        "                      id         INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "                      name  VARCHAR(80)\n" +
                        ");";
        String indexQuery = "CREATE INDEX types_name ON types (name);";
        try {
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(createQuery);
            Statement statement1 = SQLite_CONNECTION.createStatement();
            statement1.execute(indexQuery);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    public PetType get(Integer typeId, Datastores datastore) {
        PetType type = null;
        String query = "SELECT id, name FROM types WHERE id = " + typeId + ";";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                type = new PetType(resultSet.getInt("id"),
                        resultSet.getString("name"));
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        if (datastore == Datastores.H2) {
            try {
                Statement statement = H2_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                type = new PetType(resultSet.getInt("id"),
                        resultSet.getString("name"));
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return type;
    }

    public List<PetType> getAll(Datastores datastore) {
        List<PetType> types = new ArrayList<>();
        String query = "SELECT * FROM types;";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    types.add(new PetType(resultSet.getInt("id"),
                            resultSet.getString("name")));
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
                    types.add(new PetType(resultSet.getInt("id"),
                            resultSet.getString("name")));
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }

        return types;
    }

    public boolean migrate(PetType type) {
        String insertQuery = "INSERT INTO types (id, name) VALUES (" + type.getId()
                + ",'" + type.getName() + "');";
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                statement.execute(insertQuery);
            } catch (SQLException e) {
                log.error(e.getMessage());
                return false;
            }

        return true;
    }

    public void add(PetType type, Datastores datastore) {
        String insertQuery = "INSERT INTO types (id, name) VALUES (NULL"
                + ",'" + type.getName() + "');";
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

    public void update(PetType type, Datastores datastore) {
        String query = "UPDATE types SET name = '" + type.getName()
                + "' WHERE id = " + type.getId() + ";";
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
