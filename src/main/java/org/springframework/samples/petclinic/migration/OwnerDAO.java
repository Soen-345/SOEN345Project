package org.springframework.samples.petclinic.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.visit.Visit;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
        String query = "SELECT * FROM owners o LEFT JOIN pets p ON o.id = p.owner_id WHERE o.id = " + ownerId + ";";
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

                if (resultSet.getString("name") != null) {
                    Pet pet = new Pet(resultSet.getInt(7),
                            resultSet.getString("name"),
                            PetMigration.convertToLocalDate(new SimpleDateFormat("yyyy-MM-dd")
                                    .parse(resultSet.getString("birth_date"))));
                    owner.addPetNew(pet);
                    pet.setOwner(owner);

                    Statement statement1 = SQLite_CONNECTION.createStatement();
                    ResultSet resultSet1 = statement1.executeQuery("SELECT * FROM visits WHERE pet_id = "
                    + pet.getId() + ";");
                    List<Visit> visits = new ArrayList<>();
                    while (resultSet1.next()) {
                        visits.add(new Visit(resultSet1.getInt("id"),
                                resultSet1.getInt("pet_id"),
                                VisitMigration.convertToLocalDateViaInstant
                                        (new SimpleDateFormat("yyyy-MM-dd")
                                                .parse(resultSet1.getString("visit_date"))),
                                resultSet1.getString("description")));
                    }
                    System.out.println("YOOOOO: " + visits);
                    pet.setVisits(visits);

                    Statement statement2 = SQLite_CONNECTION.createStatement();
                    ResultSet resultSet2 = statement2.executeQuery("SELECT * FROM types WHERE id = "
                    + resultSet.getInt("type_id") + ";");
                    while (resultSet2.next()) {
                        pet.setType(new PetType(resultSet.getInt("id"),
                                resultSet.getString("name")));
                    }
                }
            } catch (SQLException | ParseException e){
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
                if (resultSet.getString("name") != null) {
                    Pet pet = new Pet(resultSet.getInt(7),
                            resultSet.getString("name"),
                            PetMigration.convertToLocalDate(new SimpleDateFormat("yyyy-MM-dd")
                                    .parse(resultSet.getString("birth_date"))));
                    owner.addPetNew(pet);
                    pet.setOwner(new Owner(resultSet.getInt("owner_id")));

                    Statement statement1 = H2_CONNECTION.createStatement();
                    ResultSet resultSet1 = statement1.executeQuery("SELECT * FROM visits WHERE pet_id = "
                            + pet.getId() + ";");
                    List<Visit> visits = new ArrayList<>();
                    while (resultSet1.next()) {
                        visits.add(new Visit(resultSet1.getInt("id"),
                                resultSet1.getInt("pet_id"),
                                VisitMigration.convertToLocalDateViaInstant
                                        (new SimpleDateFormat("yyyy-MM-dd")
                                                .parse(resultSet1.getString("visit_date"))),
                                resultSet1.getString("description")));
                    }
                    pet.setVisits(visits);

                    Statement statement2 = H2_CONNECTION.createStatement();
                    ResultSet resultSet2 = statement2.executeQuery("SELECT * FROM types WHERE id = "
                            + resultSet.getInt("type_id") + ";");
                    while (resultSet2.next()) {
                        pet.setType(new PetType(resultSet.getInt("id"),
                                resultSet.getString("name")));
                    }
                }
            }catch (SQLException | ParseException e){
                log.error(e.getMessage());
            }
        }
        return owner;
    }

    public List<Owner> getAll(Datastores datastore){
        List<Owner> owners = new ArrayList<>();
        String query = "SELECT * FROM owners;";
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
        if(datastore == Datastores.H2) {
            try {
                Statement statement = H2_CONNECTION.createStatement();
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

    public boolean migrate(Owner owner){
        String insertQuery = "INSERT INTO owners (id, first_name, last_name, address, city, telephone) VALUES ("+ owner.getId() + ",'"
                + owner.getFirstName() + "','" + owner.getLastName() + "','" + owner.getAddress() + "','" + owner.getCity() +
                "','" + owner.getTelephone() + "');";
            try{
                Statement statement = SQLite_CONNECTION.createStatement();
                statement.execute(insertQuery);
            }catch (SQLException e){
                log.error(e.getMessage());
                return false;
            }
        return true;
    }

    public void add(Owner owner, Datastores datastore){
        String insertQuery = "INSERT INTO owners (id, first_name, last_name, address, city, telephone) VALUES (NULL" + ",'"
                + owner.getFirstName() + "','" + owner.getLastName() + "','" + owner.getAddress() + "','" + owner.getCity() +
                "','" + owner.getTelephone() + "');";
        if (datastore == Datastores.SQLITE){
            try{
                Statement statement = SQLite_CONNECTION.createStatement();
                statement.execute(insertQuery);
            }catch (SQLException e){
                log.error(e.getMessage());
            }
        }
        if (datastore == Datastores.H2){
            try{
                Statement statement = H2_CONNECTION.createStatement();
                statement.execute(insertQuery);
            }catch (SQLException e){
                log.error(e.getMessage());
            }
        }
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
        String query;
        if (lastName == "") {
            query = "SELECT * FROM owners;";
        }
        else {
            query = "SELECT id, first_name, last_name, address, city, telephone FROM owners WHERE last_name = '" + lastName + "';";
        }
        Collection<Owner> owners = new HashSet<>();
        if(datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    Owner owner = new Owner(resultSet.getInt("id"),
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name"),
                            resultSet.getString("address"),
                            resultSet.getString("city"),
                            resultSet.getString("telephone"));
                    owners.add(owner);
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return owners;
    }

    public Collection<Owner> getByFirstName(String firstName, Datastores datastore) {
        String query;
        if (firstName == "") {
            query = "SELECT * FROM owners;";
        }
        else {
            query = "SELECT id, first_name, last_name, address, city, telephone FROM owners WHERE first_name = '" + firstName + "';";
        }
        Collection<Owner> owners = new HashSet<>();
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
