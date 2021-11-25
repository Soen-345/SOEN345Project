package org.springframework.samples.petclinic.migration;

import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.vet.VetSpecialties;
/**
 * @author Alireza Ziarizi
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class VetSpecialtiesDAO {
    private Connection SQLite_CONNECTION;
    private Connection H2_CONNECTION;

    public VetSpecialtiesDAO(){
        SQLite_CONNECTION = DatastoreConnection.connectSqlite();
        H2_CONNECTION = DatastoreConnection.connectH2();
    }

    protected void initTable(){
        String query = "DROP TABLE IF EXISTS vet_specialties;";
        try{
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(query);
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        this.createVetSpecialities();
    }

    protected  void createVetSpecialities(){
        String createQuery = "CREATE TABLE IF NOT EXISTS vet_specialties (\n" +
                "            vet_id  INTEGER,\n " +
                "            specialty_id INTEGER NOT NULL" + ");";
        try{
            Statement statement = SQLite_CONNECTION.createStatement();
            statement.execute(createQuery);
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    protected Map<Integer,VetSpecialties> getAllVetSpecialties(Datastores datastore){
        String query = "SELECT * FROM vet_specialties;";
        Map<Integer,VetSpecialties> vetSpecialties = new HashMap<>();
        if(datastore == Datastores.SQLITE){
            try{
                Statement statement = SQLite_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while(resultSet.next()){
                    vetSpecialties.put(resultSet.getInt("vet_id"), new VetSpecialties(resultSet.getInt("vet_id"),
                            resultSet.getInt("specialty_id")));
                }
            }catch (SQLException e){
                System.out.println(e.getMessage());
            }
        }
        if(datastore == Datastores.H2){
            try{
                Statement statement = H2_CONNECTION.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while(resultSet.next()){
                    vetSpecialties.put(resultSet.getInt("vet_id"), new VetSpecialties(resultSet.getInt("vet_id"),
                            resultSet.getInt("specialty_id")));
                }

            }catch (SQLException e){
                System.out.println(e.getMessage());
            }
        }
        return vetSpecialties;

    }

    protected boolean addVetSpecialties(VetSpecialties vetSpecialtie,Datastores datastore){
        String insertQuery = "INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (" + vetSpecialtie.getVet_id()
                + ",'" + vetSpecialtie.getSpecialty_id() + "');";
        if(datastore == Datastores.SQLITE){
            try{
                Statement statement = SQLite_CONNECTION.createStatement();
                statement.execute(insertQuery);
            }catch (SQLException e){
                System.out.println(e.getMessage());
                return false;
            }
        }
        if (datastore == Datastores.H2){
            try{
                Statement statement = H2_CONNECTION.createStatement();
                statement.execute(insertQuery);
            }catch (SQLException e){
                System.out.println(e.getMessage());
                return false;
            }
        }
        return true;
    }


    public void update(VetSpecialties vetSpecialtie, Datastores datastore) {
        String query = "UPDATE vet_specialties SET specialty_id = '" +
                vetSpecialtie.getSpecialty_id() + "'WHERE vet_id = "
                + vetSpecialtie.getVet_id() + ";";

        if(datastore == Datastores.SQLITE){
            try{
                Statement statement = SQLite_CONNECTION.createStatement();
                statement.execute(query);
            }catch (SQLException e){
                System.out.println(e.getMessage());
            }
        }
        if (datastore == Datastores.H2){
            try{
                Statement statement = H2_CONNECTION.createStatement();
                statement.execute(query);
            }catch (SQLException e){
                System.out.println(e.getMessage());
            }
        }
    }
}
