package org.springframework.samples.petclinic.migration;

import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetSpecialties;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alireza Ziarizi
 */
public class VetSpecialtiesMigration implements IMigration {
    private final VetSpecialtiesDAO vetSpecialtiesDAO;

    public VetSpecialtiesMigration() {
        vetSpecialtiesDAO = new VetSpecialtiesDAO();
    }

    @Override
    public int forklift() {
        this.vetSpecialtiesDAO.initTable();
        int numInsert = 0;

        Map<Integer, VetSpecialties> vetSpecialties = this.vetSpecialtiesDAO.getAllVetSpecialties(Datastores.H2);

        for(VetSpecialties vetSpecialtie : vetSpecialties.values()){
            boolean success = this.vetSpecialtiesDAO.addVetSpecialties(vetSpecialtie,Datastores.SQLITE);
            if(success){
                numInsert++;
            }
        }

        return numInsert;
    }
    public int forkliftTestOnly(Map<Integer, VetSpecialties> VetSpecialties){
        this.vetSpecialtiesDAO.initTable();
        int numInsert = 0;
        for(VetSpecialties vetSpecialtie : VetSpecialties.values()){
            boolean success = this.vetSpecialtiesDAO.addVetSpecialties(vetSpecialtie,Datastores.SQLITE);
            if(success){
                numInsert++;
            }
        }
        return numInsert;
    }

    @Override
    public int checkConsistencies() {
        int inconsistencies = 0;
        Map<Integer,VetSpecialties> expected = this.vetSpecialtiesDAO.getAllVetSpecialties(Datastores.H2);
        Map<Integer,VetSpecialties> actual = this.vetSpecialtiesDAO.getAllVetSpecialties(Datastores.SQLITE);

        for(Integer key : expected.keySet()){
            VetSpecialties expectedvetsp = expected.get(key);
            VetSpecialties actualvetsp = actual.get(key);

            if(actualvetsp == null){
                inconsistencies++;
                // log
                this.vetSpecialtiesDAO.addVetSpecialties(expectedvetsp,Datastores.SQLITE);
            }
            if(!comapre(actualvetsp,expectedvetsp)){
                inconsistencies++;
                //log
                this.vetSpecialtiesDAO.update(expectedvetsp,Datastores.SQLITE);
            }

        }
        return 0;
    }

    public int checkConsistenciesTestOnly(Map<Integer,VetSpecialties> expected) {
        Map<Integer,VetSpecialties>  actual = this.vetSpecialtiesDAO.getAllVetSpecialties(Datastores.SQLITE);
        int inconsistencies = 0;

        for(Integer key : expected.keySet()){
            VetSpecialties expectedvetsp = expected.get(key);
            VetSpecialties actualvetsp = actual.get(key);

            if(actualvetsp == null){
                inconsistencies++;
                // log
                this.vetSpecialtiesDAO.addVetSpecialties(expectedvetsp,Datastores.SQLITE);
            }
            if(!comapre(actualvetsp,expectedvetsp)){
                inconsistencies++;
                //log
                this.vetSpecialtiesDAO.update(expectedvetsp,Datastores.SQLITE);
            }

        }
        return inconsistencies;
    }

    private boolean comapre(VetSpecialties actualvetsp, VetSpecialties expectedvetsp) {
        if(actualvetsp != null && actualvetsp.getSpecialty_id() == expectedvetsp.getSpecialty_id() &&
        actualvetsp.getVet_id() == expectedvetsp.getVet_id()){
            return false;
        }
        return true;
    }

    @Override
    public boolean shadowReadWriteConsistencyChecker(Object o) {
        return false;
    }

    @Override
    public void logInconsistency(Object expected, Object actual) {

    }

    @Override
    public void closeConnections() throws SQLException {

    }
}
