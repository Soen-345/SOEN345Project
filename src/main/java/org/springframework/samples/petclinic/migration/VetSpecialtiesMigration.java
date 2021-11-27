package org.springframework.samples.petclinic.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.vet.VetSpecialty;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author Alireza Ziarizi
 */
public class VetSpecialtiesMigration implements IMigration<VetSpecialty> {

    private static final Logger log = LoggerFactory.getLogger(VisitMigration.class);

    private final VetSpecialtiesDAO vetSpecialtiesDAO;

    public VetSpecialtiesMigration() {
        vetSpecialtiesDAO = new VetSpecialtiesDAO();
    }

    public int forklift() {
        this.vetSpecialtiesDAO.initTable();
        int numInsert = 0;

        Map<Integer, VetSpecialty> vetSpecialties = this.vetSpecialtiesDAO.getAll(Datastores.H2);

        for(VetSpecialty vetSpecialtie : vetSpecialties.values()){
            boolean success = this.vetSpecialtiesDAO.add(vetSpecialtie,Datastores.SQLITE);
            if(success){
                numInsert++;
            }
        }

        return numInsert;
    }
    public int forkliftTestOnly(Map<Integer, VetSpecialty> VetSpecialties){
        this.vetSpecialtiesDAO.initTable();
        int numInsert = 0;
        for(VetSpecialty vetSpecialtie : VetSpecialties.values()){
            boolean success = this.vetSpecialtiesDAO.add(vetSpecialtie,Datastores.SQLITE);
            if(success){
                numInsert++;
            }
        }
        return numInsert;
    }

    @Override
    public int checkConsistencies() {
        int inconsistencies = 0;
        Map<Integer, VetSpecialty> expected = this.vetSpecialtiesDAO.getAll(Datastores.H2);
        Map<Integer, VetSpecialty> actual = this.vetSpecialtiesDAO.getAll(Datastores.SQLITE);

        for(Integer key : expected.keySet()){
            VetSpecialty expectedvetsp = expected.get(key);
            VetSpecialty actualvetsp = actual.get(key);

            if(actualvetsp == null){
                inconsistencies++;
                // log
                this.vetSpecialtiesDAO.add(expectedvetsp,Datastores.SQLITE);
            }
            if(!compare(actualvetsp,expectedvetsp)){
                inconsistencies++;
                logInconsistency(expectedvetsp, actualvetsp);
                this.vetSpecialtiesDAO.update(expectedvetsp,Datastores.SQLITE);
            }

        }
        return inconsistencies;
    }

    public int checkConsistenciesTestOnly(Map<Integer, VetSpecialty> expected) {
        Map<Integer, VetSpecialty>  actual = this.vetSpecialtiesDAO.getAll(Datastores.SQLITE);
        int inconsistencies = 0;

        for(Integer key : expected.keySet()){
            VetSpecialty expectedvetsp = expected.get(key);
            VetSpecialty actualvetsp = actual.get(key);

            if(actualvetsp == null){
                inconsistencies++;
                // log
                this.vetSpecialtiesDAO.add(expectedvetsp,Datastores.SQLITE);
            }
            if(!compare(actualvetsp,expectedvetsp)){
                inconsistencies++;
                //log
                this.vetSpecialtiesDAO.update(expectedvetsp,Datastores.SQLITE);
            }

        }
        return inconsistencies;
    }

    private boolean compare(VetSpecialty actualvetsp, VetSpecialty expectedvetsp) {
        if(actualvetsp != null && actualvetsp.getSpecialty_id() == expectedvetsp.getSpecialty_id() &&
        actualvetsp.getVet_id() == expectedvetsp.getVet_id()){
            return true;
        }
        return false;
    }

    public boolean shadowReadWriteConsistencyChecker(VetSpecialty vetSpecialty) {
        return false;
    }


    public void logInconsistency(VetSpecialty expected, VetSpecialty actual) {

        if (actual == null) {
            log.warn("Vet Specialty Table Inconsistency - \n " +
                    "Expected: " + expected.toString() + "\n" +
                    "Actual: NULL");
        } else {
            log.warn("Vet Specialty Table Inconsistency - \n " +
                    "Expected: " + expected.toString() + "\n"
                    + "Actual: " + actual.toString());
        }

    }

    public void closeConnections() throws SQLException {
        vetSpecialtiesDAO.closeConnections();
    }
}
