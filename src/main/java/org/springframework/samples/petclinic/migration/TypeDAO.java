package org.springframework.samples.petclinic.migration;

import org.springframework.samples.petclinic.owner.PetType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class TypeDAO {
    private Connection SQLite_CONNECTION;
    private Connection H2_CONNECTION;

    public TypeDAO() {
        SQLite_CONNECTION = DatastoreConnection.connectSqlite();
        H2_CONNECTION = DatastoreConnection.connectH2();
    }

    protected void initTable() {
        String query = "DROP TABLE IF EXISTS types;";
        try {
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        this.createTypeTable();
    }

    protected void createTypeTable() {

        String createQuery =
                "CREATE TABLE IF NOT EXISTS types (\n" +
                        "                      id         INTEGER IDENTITY PRIMARY KEY,\n" +
                        "                      name  VARCHAR(80)\n" +
                        ");";
        String indexQuery = "CREATE INDEX types_name ON types (name);";
        try {
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(createQuery);
            Statement statement1 = SQLite_CONNECTION.createStatement();
            statement1.execute(indexQuery);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    protected PetType getType(Integer typeId, Datastores datastore) {
        PetType type = null;
        String query = "SELECT id, name FROM types WHERE id = " + typeId + ";";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                type = new PetType(resultSet.getInt("id"),
                        resultSet.getString("name"));
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        if (datastore == Datastores.H2) {
            try {
                Statement statement = H2_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                type = new PetType(resultSet.getInt("id"),
                        resultSet.getString("name"));
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return type;
    }

    protected Map<Integer, PetType> getAllTypes(Datastores datastore) {
        Map<Integer, PetType> types = new HashMap<>();
        String query = "SELECT * FROM types;";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    types.put(resultSet.getInt("id"),
                            new PetType(resultSet.getInt("id"),
                                    resultSet.getString("name")));
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
                    types.put(resultSet.getInt("id"),
                            new PetType(resultSet.getInt("id"),
                                    resultSet.getString("name")));
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        return types;
    }

    protected boolean addType(PetType type, Datastores datastore) {
        String insertQuery = "INSERT INTO types (id, name) VALUES (" + type.getId()
                + ",'" + type.getName() + "');";
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

    protected void update(PetType type, Datastores datastore) {
        String query = "UPDATE types SET name = '" + type.getName()
                + "' WHERE id = " + type.getId() + ";";
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
