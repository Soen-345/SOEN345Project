package org.springframework.samples.petclinic.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.sql.SQLException;
import java.util.Objects;

public class PetMigration implements IMigration<Pet> {

    private static final Logger log = LoggerFactory.getLogger(PetMigration.class);

    private final PetDAO petDAO;

    public PetMigration() {
        petDAO = new PetDAO();
    }

    public int forklift() {

        this.petDAO.initTable();
        int numInsert = 0;

        Map<Integer, Pet> pets = this.petDAO.getAll(Datastores.H2);

        for (Pet pet : pets.values()) {
            boolean success = this.petDAO.add(pet, Datastores.SQLITE);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }

    public int forkliftTestOnly(Map<Integer, Pet> pets) {
        this.petDAO.initTable();
        int numInsert = 0;

        for (Pet pet : pets.values()) {
            boolean success = this.petDAO.add(pet, Datastores.SQLITE);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }


    public int checkConsistencies() {

        int inconsistencies = 0;

        Map<Integer, Pet> expected = this.petDAO.getAll(Datastores.H2);

        Map<Integer, Pet> actual = this.petDAO.getAll(Datastores.SQLITE);

        for (Integer key : expected.keySet()) {
            Pet exp = expected.get(key);
            Pet act = actual.get(key);
            if (act == null) {
                inconsistencies++;
                logInconsistency(exp, null);
                this.petDAO.add(exp, Datastores.SQLITE);
            }
            if (act != null && (!Objects.equals(exp.getId(), act.getId()) || !exp.getName().equals(act.getName()) ||
                    !exp.getBirthDate().equals(act.getBirthDate()) || !exp.getTypeId().equals(act.getTypeId()) || !exp.getOwnerId().equals(act.getOwnerId()))) {
                inconsistencies++;

                logInconsistency(exp, act);

                this.petDAO.update(exp, Datastores.SQLITE);
            }
        }

        return inconsistencies;
    }

    public int checkConsistenciesTestOnly(Map<Integer, Pet> expected) {

        int inconsistencies = 0;

        Map<Integer, Pet> actual = this.petDAO.getAll(Datastores.SQLITE);

        for (Integer key : expected.keySet()) {
            Pet exp = expected.get(key);
            Pet act = actual.get(key);
            if (act == null) {
                inconsistencies++;
                logInconsistency(exp, null);
                this.petDAO.add(exp, Datastores.SQLITE);
            }
            if (act != null && (!Objects.equals(exp.getId(), act.getId()) || !exp.getName().equals(act.getName()) ||
                    !exp.getBirthDate().equals(act.getBirthDate()) || !exp.getTypeId().equals(act.getTypeId()) || !exp.getOwnerId().equals(act.getOwnerId()))) {
                inconsistencies++;

                logInconsistency(exp, act);

                this.petDAO.update(exp, Datastores.SQLITE);
            }
        }

        return inconsistencies;
    }

    public boolean shadowReadWriteConsistencyChecker(Pet exp) {

        Pet act = this.petDAO.get(exp.getId(), Datastores.SQLITE);

        if (act == null) {
            this.petDAO.add(exp, Datastores.SQLITE);

            logInconsistency(exp, null);

            return false;
        }

        if (!Objects.equals(exp.getId(), act.getId()) || !exp.getName().equals(act.getName()) ||
                !exp.getBirthDate().equals(act.getBirthDate()) || !exp.getTypeId().equals(act.getTypeId()) || !exp.getOwnerId().equals(act.getOwnerId())) {

            logInconsistency(exp, act);

            this.petDAO.update(exp, Datastores.SQLITE);

            return false;
        }

        return true;
    }

    public void logInconsistency(Pet expected, Pet actual) {

        if (actual == null) {
            log.warn("Pet Table Inconsistency - \n " +
                    "Expected: " + expected.getId() + " " + expected.getName() + " " + expected.getBirthDate() + " " + expected.getTypeId() + " " + expected.getOwnerId() + "\n"
                    + "Actual: NULL");
        } else {
            log.warn("Pet Table Inconsistency - \n " +
                    "Expected: " + expected.getId() + " " + expected.getName() + " " + expected.getBirthDate() + " " + expected.getTypeId() + " " + expected.getOwnerId() + "\n"
                    + "Actual: " + actual.getId() + " " + actual.getName() + " " + actual.getBirthDate() + " " + actual.getTypeId() + " " + actual.getOwnerId());
        }
    }

    public void shadowWriteToNewDatastore(Pet pet, Owner owner) {
        pet.setOwner(owner);
        owner.addPet(pet);
        this.petDAO.add(pet, Datastores.SQLITE);
    }

    public Pet shadowRead(Integer petId) {
        return this.petDAO.get(petId, Datastores.SQLITE);
    }

    public void closeConnections() throws SQLException {
        this.petDAO.closeConnections();
    }

    public static LocalDate convertToLocalDate(java.util.Date birth_date) {
        return birth_date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}

