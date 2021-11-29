package org.springframework.samples.petclinic.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.vet.Specialty;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SpecialtiesMigration implements IMigration<Specialty> {

    private static final Logger log = LoggerFactory.getLogger(SpecialtiesMigration.class);

    private final SpecialtiesDAO specialtiesDAO;

    public SpecialtiesMigration() {
        specialtiesDAO = new SpecialtiesDAO();
    }

    public int forklift() {

        this.specialtiesDAO.initTable();
        int numInsert = 0;

        List<Specialty> specialities = this.specialtiesDAO.getAll(Datastores.H2);

        for (Specialty specialty : specialities) {
            boolean success = this.specialtiesDAO.migrate(specialty);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }

    public int forkliftTestOnly(List<Specialty> specialities) {

        this.specialtiesDAO.initTable();
        int numInsert = 0;

        for (Specialty specialty : specialities) {
            boolean success = this.specialtiesDAO.migrate(specialty);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }


    public int checkConsistencies() {

        int inconsistencies = 0;


        List<Specialty> expected = this.specialtiesDAO.getAll(Datastores.H2);

        for (Specialty exp : expected) {
            Specialty act = this.specialtiesDAO.get(exp.getId(), Datastores.SQLITE);
            if (act == null) {
                inconsistencies++;
                logInconsistency(exp, null);
                this.specialtiesDAO.add(exp, Datastores.SQLITE);
            }
            if (act != null && (!Objects.equals(exp.getId(), act.getId()) || !exp.getName().equals(act.getName()))) {
                inconsistencies++;

                logInconsistency(exp, act);

                this.specialtiesDAO.update(exp, Datastores.SQLITE);
            }
        }

        return inconsistencies;
    }

    public int checkConsistenciesTestOnly(List<Specialty> expected) {

        int inconsistencies = 0;

        for (Specialty exp : expected) {
            Specialty act = this.specialtiesDAO.get(exp.getId(), Datastores.SQLITE);
            if (act == null) {
                inconsistencies++;
                logInconsistency(exp, null);
                this.specialtiesDAO.add(exp, Datastores.SQLITE);
            }
            if (act != null && (!Objects.equals(exp.getId(), act.getId()) || !exp.getName().equals(act.getName()))) {
                inconsistencies++;

                logInconsistency(exp, act);

                this.specialtiesDAO.update(exp, Datastores.SQLITE);
            }
        }

        return inconsistencies;
    }

    public boolean shadowReadWriteConsistencyChecker(Specialty exp) {

        Specialty act = this.specialtiesDAO.get(exp.getId(), Datastores.SQLITE);

        if (act == null) {
            this.specialtiesDAO.add(exp, Datastores.SQLITE);

            logInconsistency(exp, null);

            return false;
        }

        if (!exp.getId().equals(act.getId()) || !exp.getName().equals(act.getName())) {

            logInconsistency(exp, act);

            this.specialtiesDAO.update(exp, Datastores.SQLITE);

            return false;
        }

        return true;
    }

    public void logInconsistency(Specialty expected, Specialty actual) {

        if (actual == null) {
            log.warn("Speciality Table Inconsistency - \n " +
                    "Expected: " + expected.getId() + " " + expected.getName() + "\n"
                    + "Actual: NULL");
        } else {
            log.warn("Speciality Table Inconsistency - \n " +
                    "Expected: " + expected.getId() + " " + expected.getName() + "\n"
                    + "Actual: " + actual.getId() + " " + actual.getName());
        }
    }

    public void shadowWrite(Specialty specialty) {
        this.specialtiesDAO.add(specialty, Datastores.SQLITE);
    }

    public void closeConnections() throws SQLException {
        this.specialtiesDAO.closeConnections();
    }
}
