package org.springframework.samples.petclinic.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.vet.Vet;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Alireza ziarizi
 */
public class OwnerDAO implements IDAO<Owner> {

    private static final Logger log = LoggerFactory.getLogger(OwnerDAO.class);

    private Connection SQLite_CONNECTION;
    private Connection H2_CONNECTION;


    public OwnerDAO(){
        SQLite_CONNECTION = DatastoreConnection.connectSqlite();
        H2_CONNECTION = DatastoreConnection.connectH2();
    }

    public void initTable(){
        String query = "DROP TABLE IF EXISTS owners;";
        try{
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(query);
        } catch (SQLException e){
            log.error(e.getMessage());
        }
        this.createOwnerTable();
    }

    private void createOwnerTable(){
        String createQuery =
                "CREATE TABLE IF NOT EXISTS owners (\n" +
                "                      id         INTEGER IDENTITY PRIMARY KEY,\n" +
                "                      first_name VARCHAR(30),\n" +
                "                      last_name VARCHAR(30),\n" +
                "                      address VARCHAR(255),\n" +
                "                      city VARCHAR(80),\n" +
                "                      telephone VARCHAR(20)\n" +
                ");";
        try{
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(createQuery);
        } catch (SQLException e){
            log.error(e.getMessage());
        }
    }

    public Owner get(Integer ownerId, Datastores datastore){
        Owner owner = null;
        String query = "SELECT id, first_name, last_name, address, city, telephone FROM owners WHERE id = " + ownerId + ";";
        if(datastore == Datastores.SQLITE) {
            try{
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                owner = new Owner(resultSet.getInt("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("address"),
                        resultSet.getString("city"),
                        resultSet.getString("telephone"));
            } catch (SQLException e){
                log.error(e.getMessage());
            }
        }
        if (datastore == Datastores.H2){
            try{
                Statement statement = H2_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                owner = new Owner(resultSet.getInt("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("address"),
                        resultSet.getString("city"),
                        resultSet.getString("telephone"));
            }catch (SQLException e){
                log.error(e.getMessage());
            }
        }
        return owner;
    }

    public Map<Integer, Owner> getAll(Datastores datastore){
        Map<Integer,Owner> owner = new HashMap<>();
        String query = "SELECT * FROM owners;";
        if(datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    owner.put(resultSet.getInt("id"),
                            new Owner(resultSet.getInt("id"),
                                    resultSet.getString("first_name"),
                                    resultSet.getString("last_name"),
                                    resultSet.getString("address"),
                                    resultSet.getString("city"),
                                    resultSet.getString("telephone")));
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        if(datastore == Datastores.H2) {
            try {
                Statement statement = H2_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    owner.put(resultSet.getInt("id"),
                            new Owner(resultSet.getInt("id"),
                                    resultSet.getString("first_name"),
                                    resultSet.getString("last_name"),
                                    resultSet.getString("address"),
                                    resultSet.getString("city"),
                                    resultSet.getString("telephone")));
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
     return owner;
    }

    public boolean add(Owner owner, Datastores datastore){
        String insertQuery = "INSERT INTO owners (id, first_name, last_name, address, city, telephone) VALUES (" + owner.getId() + ",'"
                + owner.getFirstName() + "','" + owner.getLastName() + "','" + owner.getAddress() + "','" + owner.getCity() +
                "','" + owner.getTelephone() + "');";
        if (datastore == Datastores.SQLITE){
            try{
                Statement statement = SQLite_CONNECTION.createStatement();
                statement.execute(insertQuery);
            }catch (SQLException e){
                log.error(e.getMessage());
                return false;
            }
        }
        if (datastore == Datastores.H2){
            try{
                Statement statement = H2_CONNECTION.createStatement();
                statement.execute(insertQuery);
            }catch (SQLException e){
                log.error(e.getMessage());
                return false;
            }
        }
        return true;
    }

    public void update(Owner owner, Datastores datastore){
        String query = "UPDATE owners SET first_name = '" + owner.getFirstName() + "', last_name = '" + owner.getLastName() +
                "', address = '" + owner.getAddress() + "', city = '" + owner.getCity() + "', telephone = '" + owner.getTelephone()
                + "' WHERE id = " + owner.getId() + ";";
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


    public Collection<Owner> getByLastName(String lastName, Datastores datastore) {
        Collection<Owner> owners = new HashSet<>();
        String query = "SELECT id, first_name, last_name, address, city, telephone FROM owners WHERE last_name = '" + lastName + "';";
        if(datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    owners.add(new Owner(resultSet.getInt("id"),
                                    resultSet.getString("first_name"),
                                    resultSet.getString("last_name"),
                                    resultSet.getString("address"),
                                    resultSet.getString("city"),
                                    resultSet.getString("telephone")));
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return owners;
    }

    public Collection<Owner> getByFirstName(String firstName, Datastores datastore) {
        Collection<Owner> owners = new HashSet<>();
        String query = "SELECT id, first_name, last_name, address, city, telephone FROM owners WHERE first_name = '" + firstName + "';";
        if(datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    owners.add(new Owner(resultSet.getInt("id"),
                                    resultSet.getString("first_name"),
                                    resultSet.getString("last_name"),
                                    resultSet.getString("address"),
                                    resultSet.getString("city"),
                                    resultSet.getString("telephone")));
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return owners;
    }



    public void closeConnections() throws SQLException {
        SQLite_CONNECTION.close();
        H2_CONNECTION.close();
    }

}
