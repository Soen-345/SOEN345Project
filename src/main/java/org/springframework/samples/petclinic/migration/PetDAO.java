package org.springframework.samples.petclinic.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.visit.Visit;


import java.sql.*;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PetDAO implements IDAO<Pet> {

    private static final Logger log = LoggerFactory.getLogger(PetDAO.class);

    private final Connection SQLite_CONNECTION;
    private final Connection H2_CONNECTION;

    public PetDAO() {
        SQLite_CONNECTION = DatastoreConnection.connectSqlite();
        H2_CONNECTION = DatastoreConnection.connectH2();
    }

    public void initTable() {
        String query = "DROP TABLE pets;";
        try {
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        this.createPetTable();
    }

    private void createPetTable() {
        String createQuery =
                "CREATE TABLE IF NOT EXISTS pets (\n" +
                        "                      id         INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "                      name VARCHAR(30),\n" +
                        "                      birth_date  DATE,\n" +
                        "               type_id INTEGER NOT NULL,\n" +
                        "             owner_id INTEGER NOT NULL, \n" +
                        "FOREIGN KEY (owner_id) REFERENCES owners (id), \n" +
                        "FOREIGN KEY (type_id) REFERENCES types (id)" +
                        ");";
        String indexQuery = "CREATE INDEX pets_name ON pets (name);";
        try {
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(createQuery);
            Statement statement1 = SQLite_CONNECTION.createStatement();
            statement1.execute(indexQuery);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    public Pet get(Integer petId, Datastores datastore) {
        Pet pet = null;
        String query = "SELECT id, name, birth_date, type_id, owner_id FROM pets WHERE id = " + petId + ";";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                if (resultSet != null) {
                    pet = new Pet(resultSet.getInt("id"),
                            resultSet.getString("name"),
                            PetMigration.convertToLocalDate(new SimpleDateFormat("yyyy-MM-dd")
                                    .parse(resultSet.getString("birth_date"))));
                    int owner_id = resultSet.getInt("owner_id");
                    int type_id = resultSet.getInt("type_id");

                    Statement statement1 = SQLite_CONNECTION.createStatement();
                    ResultSet resultSet1 = statement1.executeQuery("SELECT * FROM owners WHERE id = " + owner_id + ";");
                    if (resultSet1 != null) {
                        Owner owner = new Owner(resultSet1.getInt("id"),
                                resultSet1.getString("first_name"),
                                resultSet1.getString("last_name"),
                                resultSet1.getString("address"),
                                resultSet1.getString("city"),
                                resultSet1.getString("telephone"));
                        pet.setOwner(owner);
                    }

                    Statement statement2 = SQLite_CONNECTION.createStatement();
                    ResultSet resultSet2 = statement2.executeQuery("SELECT * FROM types WHERE id = " + type_id + ";");
                    if (resultSet2 != null) {
                        PetType type = new PetType(resultSet2.getInt("id"),
                                resultSet2.getString("name"));
                        pet.setType(type);
                    }

                    Statement statement3 = SQLite_CONNECTION.createStatement();
                    ResultSet resultSet3 = statement3.executeQuery("SELECT * FROM visits WHERE pet_id = " +
                            pet.getId() + ";");
                    List<Visit> visits = new ArrayList<>();
                    while (resultSet3.next()) {
                        Visit visit = new Visit(resultSet3.getInt("id"),
                                resultSet3.getInt("pet_id"),
                                VisitMigration.convertToLocalDateViaInstant
                                        (new SimpleDateFormat("yyyy-MM-dd")
                                                .parse(resultSet3.getString("visit_date"))),
                                resultSet3.getString("description"));
                        visits.add(visit);
                    }
                    pet.setVisits(visits);
                }
            } catch (SQLException | ParseException e) {
                log.error(e.getMessage());
            }
        }
        return pet;
    }


    public List<Pet> getAll(Datastores datastore) {
        List<Pet> pets = new ArrayList<>();
        String query = "SELECT * FROM pets;";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    Pet pet = new Pet(resultSet.getInt("id"),
                            resultSet.getString("name"),
                            PetMigration.convertToLocalDate(new SimpleDateFormat("yyyy-MM-dd")
                                    .parse(resultSet.getString("birth_date"))));

                    pet.setOwner(new Owner(resultSet.getInt("owner_id")));
                    pet.setType(new PetType(resultSet.getInt("type_id")));

                    pets.add(pet);
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
                    Pet pet = new Pet(resultSet.getInt("id"),
                            resultSet.getString("name"),
                            PetMigration.convertToLocalDate(new SimpleDateFormat("yyyy-MM-dd")
                                    .parse(resultSet.getString("birth_date"))));

                    pet.setOwner(new Owner(resultSet.getInt("owner_id")));
                    pet.setType(new PetType(resultSet.getInt("type_id")));

                    pets.add(pet);
                }
            } catch (SQLException | ParseException e) {
                log.error(e.getMessage());
            }
        }
        return pets;
    }

    public boolean migrate(Pet pet) {
        String insertQuery = "INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (" + pet.getId()
                + ",'" + pet.getName() + "','" + Date.valueOf(pet.getBirthDate()) + "'," + pet.getTypeId() + "," + pet.getOwnerId() + ");";
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                statement.executeUpdate(insertQuery);
            } catch (SQLException e) {
                log.error(e.getMessage());
                return false;
            }
        return true;
    }

    public void add(Pet pet, Datastores datastore) {
        String insertQuery = "INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (NULL, '"
                + pet.getName() + "','" + Date.valueOf(pet.getBirthDate()) + "'," + pet.getTypeId() + "," + pet.getOwnerId() + ");";
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
                statement.execute(insertQuery);
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
    }

    public void update(Pet pet, Datastores datastore) {
        String query = "UPDATE pets SET name = '" + pet.getName()
                + "', birth_date = '" + Date.valueOf(pet.getBirthDate()) + "', type_id = '" + pet.getTypeId() + "', owner_id = '" + pet.getOwnerId() +
                "' WHERE id = " + pet.getId() + ";";
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

    public Collection<Pet> getPetsByOwnerId(Integer ownerId, Datastores datastore) {
        Collection<Pet> pets = new HashSet<>();
        String query = "SELECT id, name, birth_date, type_id, owner_id FROM pets WHERE owner_id = " + ownerId + ";";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    Pet pet = new Pet(resultSet.getInt("id"),
                            resultSet.getString("name"),
                            PetMigration.convertToLocalDate(new SimpleDateFormat("yyyy-MM-dd")
                                    .parse(resultSet.getString("birth_date"))));
                    pets.add(pet);
                }
            } catch (SQLException | ParseException e) {
                log.error(e.getMessage());
            }
        }
        return pets;
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
