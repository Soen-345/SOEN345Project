package org.springframework.samples.petclinic.migration;

import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.visit.Visit;


import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alireza Ziarizi
 */
public class OwnerMigration implements IMigration<Owner> {
    private final OwnerDAO ownerDAO;

    public OwnerMigration(){
        ownerDAO = new OwnerDAO();
    }
    @Override
    public int forklift() {
        this.ownerDAO.initTable();
        int numInsert = 0;
        Map<Integer,Owner> owners = this.ownerDAO.getAllowners(Datastores.H2);

        for(Owner owner : owners.values()){
            boolean success = this.ownerDAO.addOwner(owner,Datastores.SQLITE);
            if(success){
                numInsert++;
            }
        }
        return numInsert;
    }

    public int forkliftTestOnly(Map<Integer, Owner> owners){
        this.ownerDAO.initTable();
        int numInsert = 0;
        for(Owner owner : owners.values()){
            boolean success = this.ownerDAO.addOwner(owner,Datastores.SQLITE);
            if(success){
                numInsert++;
            }
        }
        return numInsert;
    }

    @Override
    public int checkConsistencies() {
        int inconsistencies = 0;

        Map<Integer, Owner> expected =  this.ownerDAO.getAllowners(Datastores.H2);
        Map<Integer, Owner> actual =  this.ownerDAO.getAllowners(Datastores.SQLITE);

        for(Integer key : expected.keySet()){
            Owner expectedOwner = expected.get(key);
            Owner actualOwener = actual.get(key);

            if(actualOwener == null){
                inconsistencies++;
               // log
                this.ownerDAO.addOwner(expectedOwner,Datastores.SQLITE);
            }
            if(!comapre(actualOwener,expectedOwner)){
                inconsistencies++;
                //log
                this.ownerDAO.addOwner(expectedOwner,Datastores.SQLITE);
            }
        }

        return inconsistencies;
    }

    public int checkConsistenciesTestOnly(Map<Integer,Owner> expected){
        Map<Integer, Owner> actual =  this.ownerDAO.getAllowners(Datastores.SQLITE);
        int inconsistencies = 0;
        for(Integer key : expected.keySet()){
            Owner expectedOwner = expected.get(key);
            Owner actualOwener = actual.get(key);

            if(actualOwener == null){
                inconsistencies++;
                // log
                this.ownerDAO.addOwner(expectedOwner,Datastores.SQLITE);
            }
            if(!comapre(actualOwener,expectedOwner)){
                inconsistencies++;
                //log
                this.ownerDAO.addOwner(expectedOwner,Datastores.SQLITE);
            }

        }
        return inconsistencies;
    }

    public void shadowWriteToNewDatastore(Owner owner){
     this.ownerDAO.addOwner(owner,Datastores.SQLITE);

    }

    @Override
    public boolean shadowReadWriteConsistencyChecker(Owner exp) {
        Owner actual = this.ownerDAO.getOwner(exp.getId(),Datastores.SQLITE);

        if(actual == null){
            this.ownerDAO.addOwner(exp,Datastores.SQLITE);
            // log
            return false;
        }
        if(!comapre(actual,exp)){
            this.ownerDAO.addOwner(exp,Datastores.SQLITE);
            // log
            return false;
        }
        return true;
    }

    @Override
    public void logInconsistency(Owner expected, Owner actual) {

    }

    @Override
    public void closeConnections() throws SQLException {
        this.ownerDAO.closeConnections();
    }



    public int checkConsistencies(Map<Integer, Owner> owners) {
        return 0;
    }


    public void logInconsistency(Integer expected, Integer actual) {

    }
    protected boolean comapre(Owner actual,Owner expected){
        if(actual !=null && (!Objects.equals(expected.getId(),actual.getId()) || !expected.getFirstName().equals(actual.getFirstName())
                || !expected.getLastName().equals(actual.getLastName()) || !expected.getAddress().equals(actual.getAddress()) ||
                !expected.getCity().equals(actual.getCity()) || !expected.getTelephone().equals(actual.getTelephone()) )){
            return false;
        }

        return true;
    }
}
