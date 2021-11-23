package org.springframework.samples.petclinic.migration;

import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;


import java.sql.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PetDAO {

    private final Connection SQLite_CONNECTION;
    private Connection H2_CONNECTION;

    public PetDAO() {
        SQLite_CONNECTION = DatastoreConnection.connectSqlite();
        H2_CONNECTION = DatastoreConnection.connectH2();
    }

    protected void initTable() {
        String query = "DROP TABLE pets;";
        try {
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        this.createPetTable();
    }

    protected void createPetTable() {

        String createQuery =
                "CREATE TABLE IF NOT EXISTS pets (\n" +
                        "                      id         INTEGER IDENTITY PRIMARY KEY,\n" +
                        "                      name VARCHAR(30),\n" +
                        "                      birth_date  DATE,\n" +
                        "               type_id INTEGER NOT NULL,\n"+
                        "             owner_id INTEGER NOT NULL\n"+
                        ");";
        try {
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(createQuery);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    protected Pet getPet(Integer petId, Datastores datastore) {
        Pet pet = null;
        String query = "SELECT id, name, birth_date, type_id, owner_id FROM pets WHERE id = " + petId + ";";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                pet = new Pet(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        convertToLocalDate(new SimpleDateFormat("yyyy-MM-dd")
                                .parse(resultSet.getString("birth_date"))));

                pet.setOwner(new Owner(resultSet.getInt("owner_id")));
                pet.setType(new PetType(resultSet.getInt("type_id")));

            } catch (SQLException | ParseException e) {
                System.out.println(e.getMessage());
            }
        }
        if (datastore == Datastores.H2) {
            try {
                Statement statement = H2_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                pet = new Pet(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        convertToLocalDate(new SimpleDateFormat("yyyy-MM-dd")
                                .parse(resultSet.getString("birth_date"))));

                pet.setOwner(new Owner(resultSet.getInt("owner_id")));
                pet.setType(new PetType(resultSet.getInt("type_id")));

            } catch (SQLException | ParseException e) {
                System.out.println(e.getMessage());
            }
        }
        return pet;
    }

    private LocalDate convertToLocalDate(java.util.Date birth_date) {
        return birth_date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    protected Map<Integer, Pet> getAllPets(Datastores datastore) {
        Map<Integer, Pet> pets = new HashMap<>();
        String query = "SELECT * FROM pets";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    Pet pet = new Pet(resultSet.getInt("id"),
                            resultSet.getString("name"),
                            convertToLocalDate(new SimpleDateFormat("yyyy-MM-dd")
                                    .parse(resultSet.getString("birth_date"))));

                    pet.setOwner(new Owner(resultSet.getInt("owner_id")));
                    pet.setType(new PetType(resultSet.getInt("type_id")));

                    pets.put(resultSet.getInt("id"), pet);
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
                    Pet pet = new Pet(resultSet.getInt("id"),
                            resultSet.getString("name"),
                            convertToLocalDate(new SimpleDateFormat("yyyy-MM-dd")
                                    .parse(resultSet.getString("birth_date"))));

                    pet.setOwner(new Owner(resultSet.getInt("owner_id")));
                    pet.setType(new PetType(resultSet.getInt("type_id")));

                    pets.put(resultSet.getInt("id"), pet);

                }
            } catch (SQLException | ParseException e) {
                System.out.println(e.getMessage());
            }
        }

        return pets;
    }

    protected boolean addPet(Pet pet, Datastores datastore) {
        String insertQuery = "INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (" + pet.getId()
                + ",'" + pet.getName() + "','" + Date.valueOf(pet.getBirthDate()) + "','" + pet.getTypeId()+ "','"+pet.getOwnerId() + "');";
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

    protected void update(Pet pet, Datastores datastore) {
        String query = "UPDATE pets SET name = '" + pet.getName()
                + "', birth_date = '" + Date.valueOf(pet.getBirthDate()) + "', type_id = '" +pet.getTypeId()+ "', owner_id = '" +pet.getOwnerId()+
                "' WHERE id = " + pet.getId() + ";";
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
