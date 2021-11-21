package org.springframework.samples.petclinic.migration;

import org.springframework.samples.petclinic.owner.Pet;

import java.util.Map;
import java.sql.SQLException;
import java.util.Objects;

public class PetMigration {


    private final PetDAO petDAO;

    public PetMigration() {
        petDAO = new PetDAO();
    }

    public int forklift(Map<Integer, Pet> pets) {

        this.petDAO.initTable();
        int numInsert = 0;

        if (true) {
            pets = this.petDAO.getAllPets(Datastores.H2);
        }
        for (Pet pet : pets.values()) {
            boolean success = this.petDAO.addPet(pet, Datastores.SQLITE);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }


    public int checkConsistencies(Map<Integer, Pet> expected) {

        int inconsistencies = 0;

        if (!MigrationToggles.isUnderTest) {
            expected = this.petDAO.getAllPets(Datastores.H2);

        }


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
                    !exp.getBirthDate().equals(act.getBirthDate()) || !exp.getType().equals(act.getType()) || !exp.getOwner().equals(act.getOwner()))) {
                inconsistencies++;

                logInconsistency(exp, act);

                this.petDAO.update(exp, Datastores.SQLITE);
            }
        }

        return inconsistencies;
    }

    public boolean shadowReadConsistencyChecker(Pet exp) {

        Pet act = this.petDAO.getPet(exp.getId(), Datastores.SQLITE);

        if (act == null) {
            this.petDAO.addPet(exp, Datastores.SQLITE);

            logInconsistency(exp, null);

            return false;
        }

        if (!Objects.equals(exp.getId(), act.getId()) || !exp.getName().equals(act.getName()) ||
                !exp.getBirthDate().equals(act.getBirthDate()) || !exp.getType().equals(act.getType()) || !exp.getOwner().equals(act.getOwner())) {

            logInconsistency(exp, act);

            this.petDAO.update(exp, Datastores.SQLITE);

            return false;
        }

        return true;
    }

    public void logInconsistency(Pet expected, Pet actual) {

        if (actual == null) {
            System.out.println("Pet Table Inconsistency - \n " +
                    "Expected: " + expected.getId() + " " + expected.getName() + " " + expected.getBirthDate() + " " + expected.getType() + " " + expected.getOwner() + "\n"
                    + "Actual: NULL");
        } else {
            System.out.println("Pet Table Inconsistency - \n " +
                    "Expected: " + expected.getId() + " " + expected.getName() + " " + expected.getBirthDate() + " " + expected.getType() + " " + expected.getOwner() + "\n"
                    + "Actual: " + actual.getId() + " " + actual.getName() + " " + actual.getBirthDate() + " " + actual.getType() + " " + actual.getOwner());
        }
    }

    public boolean shadowWrite(Pet pet) {
        this.petDAO.addPet(pet, Datastores.SQLITE);
        return false;
    }

    public void closeConnections() throws SQLException {
        this.petDAO.closeConnections();
    }
}

