package org.springframework.samples.petclinic.migration;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.sql.SQLException;
import java.util.Objects;

@Service
public class PetMigration implements IMigration<Pet> {

    private static final Logger log = LoggerFactory.getLogger(PetMigration.class);

    private final PetDAO petDAO;
    private String dataChecker = "";

    public PetMigration() {
        petDAO = new PetDAO();
    }

    public int forklift() {

        this.petDAO.initTable();
        int numInsert = 0;

        List<Pet> pets = this.petDAO.getAll(Datastores.H2);

        for (Pet pet : pets) {
            boolean success = this.petDAO.migrate(pet);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }

    public int forkliftTestOnly(List<Pet> pets) {
        this.petDAO.initTable();
        int numInsert = 0;

        for (Pet pet : pets) {
            boolean success = this.petDAO.migrate(pet);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }


    public int checkConsistencies() {

        int inconsistencies = 0;

        List<Pet> expected = this.petDAO.getAll(Datastores.H2);

        for (int i= 0; i < expected.size(); i++) {
            Pet exp = expected.get(i);
            Pet act = this.petDAO.get(exp.getId(), Datastores.SQLITE);

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

    public int checkConsistenciesTestOnly(List<Pet> expected) {

        int inconsistencies = 0;

        for (Pet exp : expected) {
            Pet act = this.petDAO.get(exp.getId(), Datastores.SQLITE);
            if (act == null) {
                inconsistencies++;
                logInconsistency(exp, null);
                this.shadowWriteToNewDatastore(exp);
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
            this.shadowWriteToNewDatastore(exp);

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

    public void shadowUpdate(Pet pet) {
        this.petDAO.update(pet, Datastores.SQLITE);
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

    public void shadowWriteToNewDatastore(Pet pet) {
        if (MigrationToggles.isH2Enabled && MigrationToggles.isSQLiteEnabled && pet.getId() != null) {
            this.petDAO.migrate(pet);
        }
        else {
            this.petDAO.add(pet, Datastores.SQLITE);
        }
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
    public void updateData(){
        this.petDAO.addHashStorage("pet",hashable());
        log.info("Data Stored for pet");
    }

    public String hashable(){
        List<Pet> pet;
        pet = this.petDAO.getAll(Datastores.SQLITE);
        String hashStringexpected = "";
        for(int i=0; i< 4; i++){
            Pet oldpet= pet.get(i);
            hashStringexpected = hashStringexpected + oldpet.getName() + oldpet.getBirthDate() + oldpet.getTypeId()
                    + oldpet.getOwnerId();

        }
        hashStringexpected = hashValue(hashStringexpected);
        return hashStringexpected;
    }

    public boolean hashConsistencyChecker(){
        String actual = hashable();
        dataChecker = this.petDAO.getHash("pet");
        return  dataChecker.equals(actual);
    }
    private String hashValue(String value) {
        return DigestUtils.sha1Hex(value).toUpperCase();
    }

}

