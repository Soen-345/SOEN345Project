package org.springframework.samples.petclinic.migration;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.vet.VetSpecialty;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author Alireza Ziarizi
 */
public class VetSpecialtiesMigration  {

    private static final Logger log = LoggerFactory.getLogger(VisitMigration.class);

    private final VetSpecialtiesDAO vetSpecialtiesDAO;
    private String dataChecker;

    public VetSpecialtiesMigration() {
        vetSpecialtiesDAO = new VetSpecialtiesDAO();
    }

    public int forklift() {
        this.vetSpecialtiesDAO.initTable();
        int numInsert = 0;

        List<VetSpecialty> vetSpecialties = this.vetSpecialtiesDAO.getAll(Datastores.H2);

        for(VetSpecialty vetSpecialty : vetSpecialties){
            boolean success = this.vetSpecialtiesDAO.migrate(vetSpecialty);
            if(success){
                numInsert++;
            }
        }

        return numInsert;
    }
    public int forkliftTestOnly(List<VetSpecialty> VetSpecialties){
        this.vetSpecialtiesDAO.initTable();
        int numInsert = 0;

        for(VetSpecialty vetSpecialty : VetSpecialties){
            boolean success = this.vetSpecialtiesDAO.migrate(vetSpecialty);
            if(success){
                numInsert++;
            }
        }
        return numInsert;
    }


    public int checkConsistencies() {
        int inconsistencies = 0;

        List<VetSpecialty> expected = this.vetSpecialtiesDAO.getAll(Datastores.H2);

        for(VetSpecialty exp : expected){
            VetSpecialty act = this.vetSpecialtiesDAO.get(exp, Datastores.SQLITE);

            if(act == null){
                inconsistencies++;
                logInconsistency(exp, null);
                this.vetSpecialtiesDAO.add(exp, Datastores.SQLITE);
            }
        }
        return inconsistencies;
    }

    public int checkConsistenciesTestOnly(List<VetSpecialty> expected) {

        int inconsistencies = 0;

        for(VetSpecialty exp : expected){
            VetSpecialty act = this.vetSpecialtiesDAO.get(exp, Datastores.SQLITE);

            if(act == null){
                inconsistencies++;
                logInconsistency(exp, null);
                this.vetSpecialtiesDAO.add(exp, Datastores.SQLITE);
            }
        }
        return inconsistencies;
    }


    public void logInconsistency(VetSpecialty expected, VetSpecialty actual) {

        if (actual == null) {
            log.warn("Vet Specialty Table Inconsistency - \n " +
                    "Expected: " + expected.getVet_id() + " " + expected.getSpecialty_id() + "\n" +
                    "Actual: NULL");
        } else {
            log.warn("Vet Specialty Table Inconsistency - \n " +
                    "Expected: " + expected.getVet_id() + " " + expected.getSpecialty_id() + "\n"
                    + "Actual: " + actual.getVet_id() + " " + actual.getSpecialty_id());
        }

    }

    public void updateData(){
        this.vetSpecialtiesDAO.addHashStorage("vetSpecialtiesDAO",hashable());
        log.info("hashtable created for vetSpecialties");
    }
    public String hashable(){
        List<VetSpecialty> vetSpecialties;
        vetSpecialties = this.vetSpecialtiesDAO.getAll(Datastores.SQLITE);
        String hashStringexpected = "";
        for(int i=0; i< 4; i++){
            VetSpecialty oldvetSpecialtie= vetSpecialties.get(i);
            hashStringexpected = hashStringexpected + oldvetSpecialtie.getSpecialty_id() + oldvetSpecialtie.getVet_id();

        }
        hashStringexpected = hashValue(hashStringexpected);
        return hashStringexpected;
    }

    public boolean hashConsistencyChecker(){
        String actual = hashable();
        dataChecker = this.vetSpecialtiesDAO.getHash("vetSpecialtiesDAO");
        return  dataChecker.equals(actual);
    }

    private String hashValue(String value) {
        return DigestUtils.sha1Hex(value).toUpperCase();
    }

    public void closeConnections() throws SQLException {
        vetSpecialtiesDAO.closeConnections();
    }
}
