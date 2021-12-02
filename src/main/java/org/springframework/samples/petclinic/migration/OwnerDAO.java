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
import java.time.LocalDate;
import java.util.*;

/**
 * @author Alireza ziarizi
 */
public class OwnerDAO implements IDAO<Owner> {

    private static final Logger log = LoggerFactory.getLogger(OwnerDAO.class);

    private Connection SQLite_CONNECTION;
    private Connection H2_CONNECTION;


    public OwnerDAO() {
        SQLite_CONNECTION = DatastoreConnection.connectSqlite();
        H2_CONNECTION = DatastoreConnection.connectH2();
    }

    public void initTable() {
        String query = "DROP TABLE IF EXISTS owners;";
        try {
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        this.createOwnerTable();
    }

    private void createOwnerTable() {
        String createQuery =
                "CREATE TABLE IF NOT EXISTS owners (\n" +
                        "                      id         INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "                      first_name VARCHAR(30),\n" +
                        "                      last_name VARCHAR(30),\n" +
                        "                      address VARCHAR(255),\n" +
                        "                      city VARCHAR(80),\n" +
                        "                      telephone VARCHAR(20)\n" +
                        ");";
        try {
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(createQuery);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    public Owner get(Integer ownerId, Datastores datastore) {
        Owner owner = null;
        String query = "SELECT * FROM owners WHERE id = " + ownerId + ";";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                owner = new Owner(resultSet.getInt("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("address"),
                        resultSet.getString("city"),
                        resultSet.getString("telephone"));
                Statement statement1 = SQLite_CONNECTION.createStatement();
                ResultSet resultSet1 = statement1.executeQuery("SELECT * FROM pets WHERE owner_id = " + owner.getId() + ";");
                    while (resultSet1.next()) {
                        Pet pet = new Pet(resultSet1.getInt("id"), resultSet1.getString("name")
                                , PetMigration.convertToLocalDate(new SimpleDateFormat("yyyy-MM-dd")
                                .parse(resultSet1.getString("birth_date"))));
                        int type_id = resultSet1.getInt("type_id");
                        pet.setOwner(owner);

                        Statement statement2 = SQLite_CONNECTION.createStatement();
                        ResultSet resultSet2 = statement2.executeQuery("SELECT * FROM visits WHERE pet_id = "
                                + pet.getId() + ";");
                        List<Visit> visits = new ArrayList<>();
                        while (resultSet2.next()) {
                            Visit visit = new Visit(resultSet2.getInt("id"),
                                    resultSet2.getInt("pet_id"),
                                    VisitMigration.convertToLocalDateViaInstant
                                            (new SimpleDateFormat("yyyy-MM-dd")
                                                    .parse(resultSet2.getString("visit_date"))),
                                    resultSet2.getString("description"));
                            visits.add(visit);
                        }
                        pet.setVisits(visits);
                        Statement statement3 = SQLite_CONNECTION.createStatement();
                        ResultSet resultSet3 = statement3.executeQuery("SELECT * FROM types WHERE id = "
                                + type_id + ";");
                        if (resultSet3 != null) {
                            pet.setType(new PetType(resultSet3.getInt("id"),
                                    resultSet3.getString("name")));
                        }
                        owner.addPetNew(pet);
                    }
            } catch (SQLException | ParseException e) {
                log.error(e.getMessage());
            }
        }
        return owner;
    }

    public List<Owner> getAll(Datastores datastore) {
        List<Owner> owners = new ArrayList<>();
        String query = "SELECT * FROM owners;";
        if (datastore == Datastores.SQLITE) {
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
        if (datastore == Datastores.H2) {
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

    public boolean migrate(Owner owner) {
        String insertQuery = "INSERT INTO owners (id, first_name, last_name, address, city, telephone) VALUES (" + owner.getId() + ",'"
                + owner.getFirstName() + "','" + owner.getLastName() + "','" + owner.getAddress() + "','" + owner.getCity() +
                "','" + owner.getTelephone() + "');";
        try {
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.executeUpdate(insertQuery);
        } catch (SQLException e) {
            log.error(e.getMessage());
            return false;
        }
        return true;
    }

    public int add(Owner owner, Datastores datastore) {
        int id = -1;
        String insertQuery = "INSERT INTO owners (id, first_name, last_name, address, city, telephone) VALUES (NULL" + ",'"
                + owner.getFirstName() + "','" + owner.getLastName() + "','" + owner.getAddress() + "','" + owner.getCity() +
                "','" + owner.getTelephone() + "');";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                statement.executeUpdate(insertQuery);
                return statement.getGeneratedKeys().getInt(1);
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        if (datastore == Datastores.H2) {
            try {
                Statement statement = H2_CONNECTION.createStatement();
                statement.executeUpdate(insertQuery);
                return statement.getGeneratedKeys().getInt(1);
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return id;
    }

    public void update(Owner owner, Datastores datastore) {
        String query = "UPDATE owners SET first_name = '" + owner.getFirstName() + "', last_name = '" + owner.getLastName() +
                "', address = '" + owner.getAddress() + "', city = '" + owner.getCity() + "', telephone = '" + owner.getTelephone()
                + "' WHERE id = " + owner.getId() + ";";
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


    public Collection<Owner> getByLastName(String lastName) {
        String query;
        Collection<Owner> owners = new HashSet<>();
        if (lastName.equals("")) {
            query = "SELECT * FROM owners";
        } else {
            query = "SELECT * FROM owners WHERE last_name = '" + lastName + "';";
        }
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
                Statement statement1 = SQLite_CONNECTION.createStatement();
                ResultSet resultSet1 = statement1.executeQuery("SELECT * FROM pets where owner_id = " + owner.getId() + ";");
                while (resultSet1.next()) {
                    Pet pet = new Pet(resultSet1.getInt("id"),
                            resultSet1.getString("name"),
                            PetMigration.convertToLocalDate(new SimpleDateFormat("yyyy-MM-dd")
                                    .parse(resultSet1.getString("birth_date"))));
                    int type_id = resultSet1.getInt("type_id");
                    pet.setOwner(owner);

                    Statement statement2 = SQLite_CONNECTION.createStatement();
                    ResultSet resultSet2 = statement2.executeQuery("SELECT * FROM visits WHERE pet_id = "
                            + pet.getId() + ";");
                    List<Visit> visits = new ArrayList<>();
                    while (resultSet2.next()) {
                        Visit visit = new Visit(resultSet2.getInt("id"),
                                resultSet2.getInt("pet_id"),
                                VisitMigration.convertToLocalDateViaInstant
                                        (new SimpleDateFormat("yyyy-MM-dd")
                                                .parse(resultSet2.getString("visit_date"))),
                                resultSet2.getString("description"));
                        visits.add(visit);
                    }
                    pet.setVisits(visits);

                    Statement statement3 = SQLite_CONNECTION.createStatement();
                    ResultSet resultSet3 = statement3.executeQuery("SELECT * FROM types WHERE id = "
                            + type_id + ";");
                    if (resultSet3 != null) {
                        pet.setType(new PetType(resultSet3.getInt("id"),
                                resultSet3.getString("name")));
                    }
                    owner.addPetNew(pet);
                }
                owners.add(owner);
            }
        } catch (SQLException | ParseException e) {
            log.error(e.getMessage());
        }
        return owners;
    }

    public Collection<Owner> getByFirstName(String firstName) {
        String query;
        Collection<Owner> owners = new HashSet<>();
        if (firstName.equals("")) {
            query = "SELECT * FROM owners";
        } else {
            query = "SELECT * FROM owners WHERE first_name='" + firstName + "';";
        }
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
                Statement statement1 = SQLite_CONNECTION.createStatement();
                ResultSet resultSet1 = statement1.executeQuery("SELECT * FROM pets where owner_id = " + owner.getId() + ";");
                while (resultSet1.next()) {
                    Pet pet = new Pet(resultSet1.getInt("id"),
                            resultSet1.getString("name"),
                            PetMigration.convertToLocalDate(new SimpleDateFormat("yyyy-MM-dd")
                                    .parse(resultSet1.getString("birth_date"))));
                    int type_id = resultSet1.getInt("type_id");

                    pet.setOwner(owner);

                    Statement statement2 = SQLite_CONNECTION.createStatement();
                    ResultSet resultSet2 = statement2.executeQuery("SELECT * FROM visits WHERE pet_id = "
                            + pet.getId() + ";");
                    List<Visit> visits = new ArrayList<>();
                    while (resultSet2.next()) {
                        Visit visit = new Visit(resultSet2.getInt("id"),
                                resultSet2.getInt("pet_id"),
                                VisitMigration.convertToLocalDateViaInstant
                                        (new SimpleDateFormat("yyyy-MM-dd")
                                                .parse(resultSet2.getString("visit_date"))),
                                resultSet2.getString("description"));
                        visits.add(visit);
                    }
                    pet.setVisits(visits);

                    Statement statement3 = SQLite_CONNECTION.createStatement();
                    ResultSet resultSet3 = statement3.executeQuery("SELECT * FROM types WHERE id = "
                            + type_id + ";");
                    if (resultSet3 != null) {
                        pet.setType(new PetType(resultSet3.getInt("id"),
                                resultSet3.getString("name")));
                    }
                    owner.addPetNew(pet);
                }
                owners.add(owner);
            }
        } catch (SQLException | ParseException e) {
            log.error(e.getMessage());
        }
        return owners;
    }


    public void closeConnections() throws SQLException {
        SQLite_CONNECTION.close();
        H2_CONNECTION.close();
    }

}
