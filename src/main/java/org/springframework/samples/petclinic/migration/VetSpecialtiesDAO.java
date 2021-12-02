package org.springframework.samples.petclinic.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetSpecialty;
/**
 * @author Alireza Ziarizi
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VetSpecialtiesDAO {

    private static final Logger log = LoggerFactory.getLogger(VetSpecialtiesDAO.class);

    private Connection SQLite_CONNECTION;
    private Connection H2_CONNECTION;

    public VetSpecialtiesDAO(){
        SQLite_CONNECTION = DatastoreConnection.connectSqlite();
        H2_CONNECTION = DatastoreConnection.connectH2();
    }

    public void initTable(){
        String query = "DROP TABLE IF EXISTS vet_specialties;";
        try{
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(query);
        } catch (SQLException e){
            log.warn(e.getMessage());
        }
        this.createVetSpecialities();
    }

    private void createVetSpecialities(){
        String createQuery = "CREATE TABLE IF NOT EXISTS vet_specialties (\n" +
                "            vet_id  INTEGER NOT NULL,\n " +
                "            specialty_id INTEGER NOT NULL" + ");";
        try{
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(createQuery);
        } catch (SQLException e){
            log.warn(e.getMessage());
        }
    }

    public List<VetSpecialty> getAll(Datastores datastore){
        List<VetSpecialty> vetSpecialties = new ArrayList<>();
        String query = "SELECT * FROM vet_specialties;";

        if(datastore == Datastores.SQLITE){
            try{
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while(resultSet.next()){
                    vetSpecialties.add(new VetSpecialty(resultSet.getInt("vet_id"),
                            resultSet.getInt("specialty_id")));
                }
            }catch (SQLException e){
                log.warn(e.getMessage());
            }
        }
        if(datastore == Datastores.H2){
            try{
                Statement statement = H2_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while(resultSet.next()){
                    vetSpecialties.add(new VetSpecialty(resultSet.getInt("vet_id"),
                            resultSet.getInt("specialty_id")));
                }

            }catch (SQLException e){
                log.warn(e.getMessage());
            }
        }
        return vetSpecialties;

    }

    public boolean migrate(VetSpecialty vetSpecialty) {
        String insertQuery = "INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (" + vetSpecialty.getVet_id()
                + "," + vetSpecialty.getSpecialty_id() + ");";
            try{
                Statement statement = SQLite_CONNECTION.createStatement();
                statement.executeUpdate(insertQuery);
            }catch (SQLException e){
                log.warn(e.getMessage());
                return false;
            }
        return true;
    }

    public void add(VetSpecialty vetSpecialty, Datastores datastore) {
        String insertQuery = "INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (" + vetSpecialty.getVet_id()
                + "," + vetSpecialty.getSpecialty_id() + ");";
        if(datastore == Datastores.SQLITE){
            try{
                Statement statement = SQLite_CONNECTION.createStatement();
                statement.executeUpdate(insertQuery);
            }catch (SQLException e){
                log.warn(e.getMessage());
            }
        }
        if (datastore == Datastores.H2){
            try{
                Statement statement = H2_CONNECTION.createStatement();
                statement.executeUpdate(insertQuery);
            }catch (SQLException e){
                log.warn(e.getMessage());
            }
        }
    }

    public VetSpecialty get(VetSpecialty oldVetSpecialty, Datastores datastore) {
        VetSpecialty vetSpecialty = null;
        String query = "SELECT vet_id, specialty_id FROM vet_specialties WHERE vet_id = " + oldVetSpecialty.getVet_id() +
                " AND specialty_id = " + oldVetSpecialty.getSpecialty_id() + ";";
        if (datastore == Datastores.SQLITE) {
            try {
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                vetSpecialty = new VetSpecialty(resultSet.getInt("vet_id"),
                        resultSet.getInt("specialty_id"));
            } catch (SQLException e) {
                log.warn(e.getMessage());
            }
        }
        if (datastore == Datastores.H2) {
            try {
                Statement statement = H2_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                vetSpecialty = new VetSpecialty(resultSet.getInt("vet_id"),
                        resultSet.getInt("specialty_id"));
            } catch (SQLException e) {
                log.warn(e.getMessage());
            }
        }
        return vetSpecialty;
    }

    public void closeConnections() throws SQLException {
        SQLite_CONNECTION.close();
        H2_CONNECTION.close();
    }
}
