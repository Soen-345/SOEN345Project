package org.springframework.samples.petclinic.migration;

import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.sql.SQLException;
import java.util.Objects;

public class PetMigration {


    private final PetDAO petDAO;

    public PetMigration() {
        petDAO = new PetDAO();
    }

    public int forklift() {
        this.petDAO.initTable();
        int numInsert = 0;

        Map<Integer, Pet> pets = this.petDAO.getAllPets(Datastores.H2);

        for (Pet pet : pets.values()) {
            boolean success = this.petDAO.addPet(pet, Datastores.SQLITE);
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
            boolean success = this.petDAO.addPet(pet, Datastores.SQLITE);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }


    public int checkConsistencies() {

        int inconsistencies = 0;


        Map<Integer, Pet> expected = this.petDAO.getAllPets(Datastores.H2);

        Map<Integer, Pet> actual = this.petDAO.getAllPets(Datastores.SQLITE);

        for (Integer key : expected.keySet()) {
            Pet exp = expected.get(key);
            Pet act = actual.get(key);
            if (act == null) {
                inconsistencies++;
                logInconsistency(exp, null);
                this.petDAO.addPet(exp, Datastores.SQLITE);
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

        Map<Integer, Pet> actual = this.petDAO.getAllPets(Datastores.SQLITE);

        for (Integer key : expected.keySet()) {
            Pet exp = expected.get(key);
            Pet act = actual.get(key);
            if (act == null) {
                inconsistencies++;
                logInconsistency(exp, null);
                this.petDAO.addPet(exp, Datastores.SQLITE);
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

        Pet act = this.petDAO.getPet(exp.getId(), Datastores.SQLITE);

        if (act == null) {
            this.petDAO.addPet(exp, Datastores.SQLITE);

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
            System.out.println("Pet Table Inconsistency - \n " +
                    "Expected: " + expected.getId() + " " + expected.getName() + " " + expected.getBirthDate() + " " + expected.getTypeId() + " " + expected.getOwnerId() + "\n"
                    + "Actual: NULL");
        } else {
            System.out.println("Pet Table Inconsistency - \n " +
                    "Expected: " + expected.getId() + " " + expected.getName() + " " + expected.getBirthDate() + " " + expected.getTypeId() + " " + expected.getOwnerId() + "\n"
                    + "Actual: " + actual.getId() + " " + actual.getName() + " " + actual.getBirthDate() + " " + actual.getTypeId() + " " + actual.getOwnerId());
        }
    }

    public void shadowWriteToNewDatastore(Pet pet, Owner owner) {
        pet.setOwner(owner);
        this.petDAO.addPet(pet, Datastores.SQLITE);
    }

    public void closeConnections() throws SQLException {
        this.petDAO.closeConnections();
    }
    public static LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}

