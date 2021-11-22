package org.springframework.samples.petclinic.migration;

import org.springframework.samples.petclinic.owner.Owner;


import javax.xml.crypto.Data;
import java.util.Map;
/**
 * @author Alireza Ziarizi
 */
public class OwnerMigration {

    private void initTable() {
        String deleteQuery = "DROP TABLE IF EXISTS vets";
        QueryController.queryVets(deleteQuery, Datastore.SQLITE, false);

        String createQuery = "CREATE TABLE onwers (\n" +
                "                      id         INTEGER IDENTITY PRIMARY KEY,\n" +
                "                      first_name VARCHAR(30),\n" +
                "                      last_name VARCHAR(30),\n" +
                "                      address VARCHAR(255),\n" +
                "                      city VARCHAR(80),\n" +
                "                      telephone VARCHAR(20),\n" +
                ");\n" +
                "CREATE INDEX vets_last_name ON vets (last_name);";
        QueryController.queryVets(createQuery, Datastore.SQLITE, false);
    }

    public void forklift(Map<Integer, Owner> owners) {
        if(!DatastoreToggles.isUnderTest){
            String selectQuery = "SELECT * FROM owners";
            owners = QueryController.queryOwners(selectQuery,Datastore.H2,true);
        }

        this.initTable();
        for (Owner owner : owners.values()){
            String insterQuery = "INSERT INTO owners (id, first_name, last_name, address, city, telephone ) VALUES ('"
                    + owner.getId() +  "','"
                    + owner.getFirstName() +  "','"
                    + owner.getLastName() +  "','"
                    + owner.getAddress() +  "','"
                    + owner.getCity() +   "','"
                    + owner.getTelephone() + "');";
            QueryController.queryOwners(insterQuery,Datastore.SQLITE,false);
        }

    }


    public int checkConsistencies(Map<Integer, Owner> owners) {
        return 0;
    }


    public void logInconsistency(Integer expected, Integer actual) {

    }
}
