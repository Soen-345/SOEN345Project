package org.springframework.samples.petclinic.migration;

import org.springframework.samples.petclinic.vet.Specialty;
import org.springframework.samples.petclinic.vet.Vet;

import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

public class SpecialtyMigration {
    private final SpecialtyDAO specialtyDAO;

    public SpecialtyMigration() {
        specialtyDAO = new SpecialtyDAO();
    }

    public int forklift() {
        this.specialtyDAO.initTable();
        int numInsert = 0;

        Map<Integer, Specialty> specialities = this.specialtyDAO.getAllSpecialities(Datastores.H2);

        for (Specialty specialty : specialities.values()) {
            boolean success = this.specialtyDAO.addSpecialty(specialty, Datastores.SQLITE);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }

    public int forkliftTestOnly(Map<Integer, Specialty> specialities) {

        this.specialtyDAO.initTable();
        int numInsert = 0;

        for (Specialty specialty : specialities.values()) {
            boolean success = this.specialtyDAO.addSpecialty(specialty, Datastores.SQLITE);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }


    public int checkConsistencies() {

        int inconsistencies = 0;


        Map<Integer, Specialty> expected = this.specialtyDAO.getAllSpecialities(Datastores.H2);

        Map<Integer, Specialty> actual = this.specialtyDAO.getAllSpecialities(Datastores.SQLITE);

        for (Integer key : expected.keySet()) {
            Specialty exp = expected.get(key);
            Specialty act = actual.get(key);
            if (act == null) {
                inconsistencies++;
                logInconsistency(exp, null);
                this.specialtyDAO.addSpecialty(exp, Datastores.SQLITE);
            }
            if (act != null && (!Objects.equals(exp.getId(), act.getId()) || !exp.getName().equals(act.getName()) )) {
                inconsistencies++;

                logInconsistency(exp, act);

                this.specialtyDAO.update(exp, Datastores.SQLITE);
            }
        }

        return inconsistencies;
    }

    public int checkConsistenciesTestOnly(Map<Integer, Specialty> expected) {

        int inconsistencies = 0;

        Map<Integer, Specialty> actual = this.specialtyDAO.getAllSpecialities(Datastores.SQLITE);

        for (Integer key : expected.keySet()) {
            Specialty exp = expected.get(key);
            Specialty act = actual.get(key);
            if (act == null) {
                inconsistencies++;
                logInconsistency(exp, null);
                this.specialtyDAO.addSpecialty(exp, Datastores.SQLITE);
            }
            if (act != null && (!Objects.equals(exp.getId(), act.getId()) || !exp.getName().equals(act.getName()) )) {
                inconsistencies++;

                logInconsistency(exp, act);

                this.specialtyDAO.update(exp, Datastores.SQLITE);
            }
        }

        return inconsistencies;
    }

    public boolean shadowReadConsistencyChecker(Specialty exp) {

        Specialty act = this.specialtyDAO.getSpecialty(exp.getId(), Datastores.SQLITE);

        if (act == null) {
            this.specialtyDAO.addSpecialty(exp, Datastores.SQLITE);

            logInconsistency(exp, null);

            return false;
        }

        if (!exp.getId().equals(act.getId()) || !exp.getName().equals(act.getName())) {

            logInconsistency(exp, act);

            this.specialtyDAO.update(exp, Datastores.SQLITE);

            return false;
        }

        return true;
    }

    public void logInconsistency(Specialty expected, Specialty actual) {

        if (actual == null) {
            System.out.println("Speciality Table Inconsistency - \n " +
                    "Expected: " + expected.getId() + " " +   expected.getName() + "\n"
                    + "Actual: NULL");
        } else {
            System.out.println("Speciality Table Inconsistency - \n " +
                    "Expected: " + expected.getId() + " "  + expected.getName() + "\n"
                    + "Actual: " + actual.getId() + " "  + actual.getName());
        }
    }

    public void shadowWrite(Specialty specialty) {
        this.specialtyDAO.addSpecialty(specialty, Datastores.SQLITE);
    }

    public void closeConnections() throws SQLException {
        this.specialtyDAO.closeConnections();
    }
}
