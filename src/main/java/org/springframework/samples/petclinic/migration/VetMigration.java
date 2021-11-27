package org.springframework.samples.petclinic.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.vet.Vet;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * @author Sevag Eordkian
 * Shadow writes not applicable for Vets, the system doesn't let users create a vet.
 */

public class VetMigration implements IMigration<Vet>{

    private static final Logger log = LoggerFactory.getLogger(VetMigration.class);

    private final VetDAO vetDAO;

    public VetMigration() {
        vetDAO = new VetDAO();
    }

    public int forklift() {

        this.vetDAO.initTable();
        int numInsert = 0;

        Map<Integer, Vet> vets = this.vetDAO.getAll(Datastores.H2);

        for (Vet vet : vets.values()) {
            boolean success = this.vetDAO.add(vet, Datastores.SQLITE);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }

    public int forkliftTestOnly(Map<Integer, Vet> vets) {

        this.vetDAO.initTable();
        int numInsert = 0;

        for (Vet vet : vets.values()) {
            boolean success = this.vetDAO.add(vet, Datastores.SQLITE);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }


    public int checkConsistencies() {

        int inconsistencies = 0;

        Map<Integer, Vet> expected = this.vetDAO.getAll(Datastores.H2);

        Map<Integer, Vet> actual = this.vetDAO.getAll(Datastores.SQLITE);

        for (Integer key : expected.keySet()) {
            Vet exp = expected.get(key);
            Vet act = actual.get(key);
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

    public int checkConsistenciesTestOnly(Map<Integer, Vet> expected) {

        int inconsistencies = 0;

        Map<Integer, Vet> actual = this.vetDAO.getAll(Datastores.SQLITE);

        for (Integer key : expected.keySet()) {
            Vet exp = expected.get(key);
            Vet act = actual.get(key);
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
        return this.vetDAO.getAll(Datastores.SQLITE).values();
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
        this.vetDAO.add(vet, Datastores.SQLITE);
    }

    public void closeConnections() throws SQLException {
        this.vetDAO.closeConnections();
    }
}
