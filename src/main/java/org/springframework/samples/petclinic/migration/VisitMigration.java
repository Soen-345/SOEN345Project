package org.springframework.samples.petclinic.migration;

import org.springframework.samples.petclinic.visit.Visit;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;


/**
 * @author Sevag Eordkian
 */
public class VisitMigration implements IMigration<Visit> {

    private final VisitDAO visitDAO;

    public VisitMigration() {
        visitDAO = new VisitDAO();
    }

    public int forklift() {
        this.visitDAO.initTable();
        int numInsert = 0;

        Map<Integer, Visit> visits = this.visitDAO.getAllVisits(Datastores.H2);

        for (Visit visit : visits.values()) {
            boolean success = this.visitDAO.addVisit(visit, Datastores.SQLITE);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }

    public int forkliftTestOnly(Map<Integer, Visit> visits) {

        this.visitDAO.initTable();
        int numInsert = 0;

        for (Visit visit : visits.values()) {
            boolean success = this.visitDAO.addVisit(visit, Datastores.SQLITE);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }

    public int checkConsistencies() {

        int inconsistencies = 0;

        Map<Integer, Visit> expected = this.visitDAO.getAllVisits(Datastores.H2);

        Map<Integer, Visit> actual = this.visitDAO.getAllVisits(Datastores.SQLITE);

        for (Integer key : expected.keySet()) {
            Visit exp = expected.get(key);
            Visit act = actual.get(key);
            if (act == null) {
                inconsistencies++;
                logInconsistency(exp, null);
                this.visitDAO.addVisit(exp, Datastores.SQLITE);
            }
            if (act != null && (!exp.getId().equals(act.getId()) || !exp.getPetId().equals(act.getPetId()) ||
                    !exp.getDate().equals(act.getDate()) || !exp.getDescription().equals(act.getDescription()))) {
                inconsistencies++;

                logInconsistency(exp, act);

                this.visitDAO.update(exp, Datastores.SQLITE);
            }
        }

        return inconsistencies;
    }
    public int checkConsistenciesTestOnly(Map<Integer, Visit> expected) {

        int inconsistencies = 0;

        Map<Integer, Visit> actual = this.visitDAO.getAllVisits(Datastores.SQLITE);

        for (Integer key : expected.keySet()) {
            Visit exp = expected.get(key);
            Visit act = actual.get(key);
            if (act == null) {
                inconsistencies++;
                logInconsistency(exp, null);
                this.visitDAO.addVisit(exp, Datastores.SQLITE);
            }
            if (act != null && (!exp.getId().equals(act.getId()) || !exp.getPetId().equals(act.getPetId()) ||
                    !exp.getDate().equals(act.getDate()) || !exp.getDescription().equals(act.getDescription()))) {
                inconsistencies++;

                logInconsistency(exp, act);

                this.visitDAO.update(exp, Datastores.SQLITE);
            }
        }

        return inconsistencies;
    }

    public boolean shadowReadWriteConsistencyChecker(Visit exp) {

        Visit act = this.visitDAO.getVisit(exp.getId(), Datastores.SQLITE);

        if (act == null) {
            this.visitDAO.addVisit(exp, Datastores.SQLITE);

            logInconsistency(exp, null);

            return false;
        }

        if (!exp.getId().equals(act.getId()) || !exp.getPetId().equals(act.getPetId()) ||
                !exp.getDate().equals(act.getDate()) || !exp.getDescription().equals(act.getDescription())) {

            logInconsistency(exp, act);

            this.visitDAO.update(exp, Datastores.SQLITE);

            return false;
        }

        return true;
    }

    public void logInconsistency(Visit expected, Visit actual) {

        if (actual == null) {
            System.out.println("Visit Table Inconsistency - \n " +
                    "Expected: " + expected.getId() + " " + expected.getPetId() + " " + expected.getDate() + " " + expected.getDescription() + "\n"
                    + "Actual: NULL");
        } else {
            System.out.println("Visit Table Inconsistency - \n " +
                    "Expected: " + expected.getId() + " " + expected.getPetId() + " " + expected.getDate() + " " + expected.getDescription() + "\n"
                    + "Actual: " + actual.getId() + " " + actual.getPetId() + " " + actual.getDate() + " " + actual.getDescription());
        }
    }

    public void shadowWriteToNewDatastore(Visit visit) {
        this.visitDAO.addVisit(visit, Datastores.SQLITE);
    }

    public void closeConnections() throws SQLException {
        this.visitDAO.closeConnections();
    }

    public static LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

}
