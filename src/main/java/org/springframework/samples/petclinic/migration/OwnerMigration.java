package org.springframework.samples.petclinic.migration;

import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;


import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alireza Ziarizi
 */
public class OwnerMigration implements IMigration<Owner> {
    private final OwnerDAO ownerDAO;
    private final PetDAO petDAO;

    public OwnerMigration() {
        ownerDAO = new OwnerDAO();
        petDAO = new PetDAO();
    }

    public int forklift() {
        this.ownerDAO.initTable();
        int numInsert = 0;
        Map<Integer, Owner> owners = this.ownerDAO.getAll(Datastores.H2);

        for (Owner owner : owners.values()) {
            boolean success = this.ownerDAO.add(owner, Datastores.SQLITE);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }

    public int forkliftTestOnly(Map<Integer, Owner> owners) {
        this.ownerDAO.initTable();
        int numInsert = 0;
        for (Owner owner : owners.values()) {
            boolean success = this.ownerDAO.add(owner, Datastores.SQLITE);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }

    public int checkConsistencies() {
        int inconsistencies = 0;

        Map<Integer, Owner> expected = this.ownerDAO.getAll(Datastores.H2);
        Map<Integer, Owner> actual = this.ownerDAO.getAll(Datastores.SQLITE);

        for (Integer key : expected.keySet()) {
            Owner expectedOwner = expected.get(key);
            Owner actualOwener = actual.get(key);

            if (actualOwener == null) {
                inconsistencies++;
                // log
                this.ownerDAO.add(expectedOwner, Datastores.SQLITE);
            }
            if (!compare(actualOwener, expectedOwner)) {
                inconsistencies++;
                //log
                this.ownerDAO.update(expectedOwner, Datastores.SQLITE);
            }
        }

        return inconsistencies;
    }

    public int checkConsistenciesTestOnly(Map<Integer, Owner> expected) {
        Map<Integer, Owner> actual = this.ownerDAO.getAll(Datastores.SQLITE);
        int inconsistencies = 0;
        for (Integer key : expected.keySet()) {
            Owner expectedOwner = expected.get(key);
            Owner actualOwener = actual.get(key);

            if (actualOwener == null) {
                inconsistencies++;
                // log
                this.ownerDAO.add(expectedOwner, Datastores.SQLITE);
            }
            if (!compare(actualOwener, expectedOwner)) {
                inconsistencies++;
                //log
                this.ownerDAO.update(expectedOwner, Datastores.SQLITE);
            }

        }
        return inconsistencies;
    }

    public void shadowWriteToNewDatastore(Owner owner) {
        this.ownerDAO.add(owner, Datastores.SQLITE);

    }


    public boolean shadowReadWriteConsistencyChecker(Owner exp) {
        Owner actual = this.ownerDAO.get(exp.getId(), Datastores.SQLITE);

        if (actual == null) {
            this.ownerDAO.add(exp, Datastores.SQLITE);
            // log
            return false;
        }
        if (!compare(actual, exp)) {
            this.ownerDAO.update(exp, Datastores.SQLITE);
            // log
            return false;
        }
        return true;
    }

    public Owner shadowRead(Integer ownerId) {
        Owner owner = this.ownerDAO.get(ownerId, Datastores.SQLITE);
        Collection<Pet> pets = this.petDAO.getPetsByOwnerId(owner.getId(), Datastores.SQLITE);
        for (Pet pet : pets) {
            owner.addPet(pet);
        }
        return owner;
    }

    public Collection<Owner> shadowReadByLastName(String lastName) {
        return this.ownerDAO.getByLastName(lastName, Datastores.SQLITE);
    }

    public Collection<Owner> shadowReadByFirstName(String firstName) {
        return this.ownerDAO.getByFirstName(firstName, Datastores.SQLITE);
    }


    public void logInconsistency(Owner expected, Owner actual) {

    }


    public void closeConnections() throws SQLException {
        this.ownerDAO.closeConnections();
    }

    private boolean compare(Owner actual, Owner expected) {
        if (actual != null && (!Objects.equals(expected.getId(), actual.getId()) || !expected.getFirstName().equals(actual.getFirstName())
                || !expected.getLastName().equals(actual.getLastName()) || !expected.getAddress().equals(actual.getAddress()) ||
                !expected.getCity().equals(actual.getCity()) || !expected.getTelephone().equals(actual.getTelephone()))) {
            return false;
        }
        return true;
    }
}
