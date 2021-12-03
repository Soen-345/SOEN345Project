package org.springframework.samples.petclinic.migration;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author Sevag Eordkian
 * Shadow writes not applicable for Vets, the system doesn't let users create a vet.
 */
@Service
public class VetMigration implements IMigration<Vet>{

    private static final Logger log = LoggerFactory.getLogger(VetMigration.class);

    private final VetDAO vetDAO;
    private String datachecker = "";

    public VetMigration() {
        vetDAO = new VetDAO();
    }

    public int forklift() {

        this.vetDAO.initTable();
        int numInsert = 0;

        List<Vet> vets = this.vetDAO.getAll(Datastores.H2);

        for (Vet vet : vets) {
            boolean success = this.vetDAO.migrate(vet);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }

    public int forkliftTestOnly(List<Vet> vets) {

        this.vetDAO.initTable();
        int numInsert = 0;

        for (Vet vet : vets) {
            boolean success = this.vetDAO.migrate(vet);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }


    public int checkConsistencies() {

        int inconsistencies = 0;

        List<Vet> expected = this.vetDAO.getAll(Datastores.H2);

        for (Vet exp : expected) {
            Vet act = this.vetDAO.get(exp.getId(), Datastores.SQLITE);
            if (act == null) {
                inconsistencies++;
                logInconsistency(exp, null);
                this.vetDAO.add(exp, Datastores.SQLITE);
            }
            if (act != null && (!Objects.equals(exp.getId(), act.getId()) || !exp.getFirstName().equals(act.getFirstName()) ||
                    !exp.getLastName().equals(act.getLastName()))) {
                inconsistencies++;

                logInconsistency(exp, act);

                this.vetDAO.update(exp, Datastores.SQLITE);
            }
        }

        return inconsistencies;
    }

    public int checkConsistenciesTestOnly(List<Vet> expected) {

        int inconsistencies = 0;

        for (Vet exp : expected) {
            Vet act = this.vetDAO.get(exp.getId(), Datastores.SQLITE);
            if (act == null) {
                inconsistencies++;
                logInconsistency(exp, null);
                this.shadowWriteToNewDatastore(exp);
            }
            if (act != null && (!Objects.equals(exp.getId(), act.getId()) || !exp.getFirstName().equals(act.getFirstName()) ||
                    !exp.getLastName().equals(act.getLastName()))) {
                inconsistencies++;

                logInconsistency(exp, act);

                this.vetDAO.update(exp, Datastores.SQLITE);
            }
        }

        return inconsistencies;
    }

    public boolean shadowReadWriteConsistencyChecker(Vet exp) {

        Vet act = this.vetDAO.get(exp.getId(), Datastores.SQLITE);

        if (act == null) {
            this.vetDAO.add(exp, Datastores.SQLITE);

            logInconsistency(exp, null);

            return false;
        }

        if (!exp.getId().equals(act.getId()) || !exp.getFirstName().equals(act.getFirstName()) ||
                !exp.getLastName().equals(act.getLastName())) {

            logInconsistency(exp, act);

            this.vetDAO.update(exp, Datastores.SQLITE);

            return false;
        }

        return true;
    }

    public Collection<Vet> findAll() {
        return this.vetDAO.getAll(Datastores.SQLITE);
    }

    public void logInconsistency(Vet expected, Vet actual) {

        if (actual == null) {
            log.warn("Vet Table Inconsistency - \n " +
                    "Expected: " + expected.getId() + " " + expected.getFirstName() + " " + expected.getLastName() + "\n"
                    + "Actual: NULL");
        } else {
            log.warn("Vet Table Inconsistency - \n " +
                    "Expected: " + expected.getId() + " " + expected.getFirstName() + " " + expected.getLastName() + "\n"
                    + "Actual: " + actual.getId() + " " + actual.getFirstName() + " " + actual.getLastName());
        }
    }

    public void shadowWriteToNewDatastore(Vet vet) {
        if (MigrationToggles.isUnderTest) {
            this.vetDAO.migrate(vet);
        }
        else {
            this.vetDAO.add(vet, Datastores.SQLITE);
        }
    }

    public void updateData(){
       this.vetDAO.addHashStorage("vet",hashable());
       log.info("Data Stored for vet");
    }

    public String hashable(){
        List <Vet> vet;
        vet = this.vetDAO.getAll(Datastores.SQLITE);
        String hashStringExpected = "";
        for(int i=0; i<4; i++){
            Vet oldvet = vet.get(i);
            hashStringExpected = hashStringExpected + oldvet.getFirstName() + oldvet.getLastName();
        }
        hashStringExpected = hashValue(hashStringExpected);
     return hashStringExpected;
    }

    public boolean hashConsistencyChecker(){
        String actual = hashable();
        datachecker = this.vetDAO.getHash("vet");
        return datachecker.equals(actual);
    }
    public String hashValue(String value){
        return DigestUtils.sha1Hex(value).toUpperCase();
    }

    public void closeConnections() throws SQLException {
        this.vetDAO.closeConnections();
    }
}
