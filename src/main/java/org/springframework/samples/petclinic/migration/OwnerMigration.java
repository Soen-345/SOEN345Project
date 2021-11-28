package org.springframework.samples.petclinic.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;


import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alireza Ziarizi
 */
public class OwnerMigration implements IMigration<Owner> {

    private static final Logger log = LoggerFactory.getLogger(OwnerMigration.class);

    private final OwnerDAO ownerDAO;

    public OwnerMigration() {
        ownerDAO = new OwnerDAO();
    }

    public int forklift() {

        this.ownerDAO.initTable();
        int numInsert = 0;
        List<Owner> owners = this.ownerDAO.getAll(Datastores.H2);

        for (Owner owner : owners) {
            boolean success = this.ownerDAO.migrate(owner);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }

    public int forkliftTestOnly(List<Owner> owners) {
        this.ownerDAO.initTable();
        int numInsert = 0;
        for (Owner owner : owners) {
            boolean success = this.ownerDAO.migrate(owner);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }

    public int checkConsistencies() {
        int inconsistencies = 0;

        List<Owner> expected = this.ownerDAO.getAll(Datastores.H2);

        for (Owner expectedOwner : expected) {
            Owner actualOwner = this.ownerDAO.get(expectedOwner.getId(), Datastores.SQLITE);

            if (actualOwner == null) {
                inconsistencies++;
                logInconsistency(expectedOwner, null);
                this.ownerDAO.add(expectedOwner, Datastores.SQLITE);
            }
            if (!compare(actualOwner, expectedOwner)) {
                inconsistencies++;

                logInconsistency(expectedOwner, actualOwner);
                this.ownerDAO.update(expectedOwner, Datastores.SQLITE);
            }
        }

        return inconsistencies;
    }

    public int checkConsistenciesTestOnly(List<Owner> expected) {
        int inconsistencies = 0;

        for (int i= 0; i < expected.size(); i++) {
            Owner expectedOwner = expected.get(i);
            Owner actualOwner = this.ownerDAO.get(expectedOwner.getId(), Datastores.SQLITE);

            if (actualOwner == null) {
                inconsistencies++;
                logInconsistency(expectedOwner, null);
                this.shadowWriteToNewDatastore(expectedOwner);
            }
            if (!compare(actualOwner, expectedOwner)) {
                inconsistencies++;
                logInconsistency(expectedOwner, actualOwner);
                this.ownerDAO.update(expectedOwner, Datastores.SQLITE);
            }

        }
        return inconsistencies;
    }

    public void shadowWriteToNewDatastore(Owner owner) {
        if (MigrationToggles.isH2Enabled && MigrationToggles.isSQLiteEnabled && MigrationToggles.isUnderTest) {
            this.ownerDAO.migrate(owner);
        }
        else {
            this.ownerDAO.add(owner, Datastores.SQLITE);
        }
    }


    public boolean shadowReadWriteConsistencyChecker(Owner exp) {
        Owner actual = this.ownerDAO.get(exp.getId(), Datastores.SQLITE);

        if (actual == null) {
            this.shadowWriteToNewDatastore(exp);
            logInconsistency(exp, null);
            return false;
        }
        if (!compare(actual, exp)) {
            this.ownerDAO.update(exp, Datastores.SQLITE);
            logInconsistency(exp, actual);
            return false;
        }
        return true;
    }

    public Owner shadowRead(Integer ownerId) {
        return this.ownerDAO.get(ownerId, Datastores.SQLITE);
    }

    public Collection<Owner> shadowReadByLastName(String lastName) {
        return this.ownerDAO.getByLastName(lastName, Datastores.SQLITE);
    }

    public Collection<Owner> shadowReadByFirstName(String firstName) {
        return this.ownerDAO.getByFirstName(firstName, Datastores.SQLITE);
    }


    public void logInconsistency(Owner expected, Owner actual) {
        if (actual == null) {
            log.warn("Owner Table Inconsistency - \n " +
                    "Expected: " + expected.toString() + "\n" +
                    "Actual: NULL");
        } else {
            log.warn("Owner Table Inconsistency - \n " +
                    "Expected: " + expected.toString() + "\n"
                    + "Actual: " + actual.toString());
        }
    }


    private boolean compare(Owner actual, Owner expected) {
        if (actual != null && (!Objects.equals(expected.getId(), actual.getId()) || !expected.getFirstName().equals(actual.getFirstName())
                || !expected.getLastName().equals(actual.getLastName()) || !expected.getAddress().equals(actual.getAddress()) ||
                !expected.getCity().equals(actual.getCity()) || !expected.getTelephone().equals(actual.getTelephone()))) {
            return false;
        }
        return true;
    }

    public void closeConnections() throws SQLException {
        this.ownerDAO.closeConnections();
    }
}
