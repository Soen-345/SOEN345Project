package org.springframework.samples.petclinic.migration;

import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.visit.Visit;


import javax.xml.crypto.Data;
import java.util.Map;
/**
 * @author Alireza Ziarizi
 */
public class OwnerMigration {
    private final OwnerDAO ownerDAO;

    public OwnerMigration(){
        ownerDAO = new OwnerDAO();
    }

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

    public int checkConsistencies(Map<Integer, Owner> owners) {
        return 0;
    }


    public void logInconsistency(Integer expected, Integer actual) {

    }
}
