package org.springframework.samples.petclinic.migration;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.visit.Visit;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @author Sevag Eordkian
 */
@Service
public class VisitMigration implements IMigration<Visit> {

    private static final Logger log = LoggerFactory.getLogger(VisitMigration.class);

    private final VisitDAO visitDAO;
    private String dataChecker;

    public VisitMigration() {
        visitDAO = new VisitDAO();
    }

    public int forklift() {

        this.visitDAO.initTable();
        int numInsert = 0;

        List<Visit> visits = this.visitDAO.getAll(Datastores.H2);

        for (Visit visit : visits) {
            boolean success = this.visitDAO.migrate(visit);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }

    public int forkliftTestOnly(List<Visit> visits) {

        this.visitDAO.initTable();
        int numInsert = 0;

        for (Visit visit : visits) {
            boolean success = this.visitDAO.migrate(visit);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }

    public int checkConsistencies() {

        int inconsistencies = 0;

        List<Visit> expected = this.visitDAO.getAll(Datastores.H2);

        for (Visit exp : expected) {
            Visit act = this.visitDAO.get(exp.getId(), Datastores.SQLITE);

            if (act == null) {
                inconsistencies++;
                logInconsistency(exp, null);
                this.visitDAO.add(exp, Datastores.SQLITE);
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
    public int checkConsistenciesTestOnly(List<Visit> expected) {

        int inconsistencies = 0;

        for (Visit exp : expected) {
            Visit act = this.visitDAO.get(exp.getId(), Datastores.SQLITE);
            if (act == null) {
                inconsistencies++;
                logInconsistency(exp, null);
                this.shadowWriteToNewDatastore(exp);
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

        Visit act = this.visitDAO.get(exp.getId(), Datastores.SQLITE);

        if (act == null) {
            this.shadowWriteToNewDatastore(exp);

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

    public List<Visit> shadowReadByPetId(Integer petId) {
        return this.visitDAO.getByPetId(petId, Datastores.SQLITE);
    }

    public void logInconsistency(Visit expected, Visit actual) {

        if (actual == null) {
            log.warn("Visit Table Inconsistency - \n " +
                    "Expected: " + expected.getId() + " " + expected.getPetId() + " " + expected.getDate() + " " + expected.getDescription() + "\n"
                    + "Actual: NULL");
        } else {
            log.warn("Visit Table Inconsistency - \n " +
                    "Expected: " + expected.getId() + " " + expected.getPetId() + " " + expected.getDate() + " " + expected.getDescription() + "\n"
                    + "Actual: " + actual.getId() + " " + actual.getPetId() + " " + actual.getDate() + " " + actual.getDescription());
        }
    }


    public void shadowWriteToNewDatastore(Visit visit) {
        if (MigrationToggles.isH2Enabled && MigrationToggles.isSQLiteEnabled && visit.getId() != null) {
            this.visitDAO.migrate(visit);
        }
        else {
            this.visitDAO.add(visit, Datastores.SQLITE);
        }
    }

    public void closeConnections() throws SQLException {
        this.visitDAO.closeConnections();
    }
    public void updateData(){
        this.visitDAO.addHashStorage("visit",hashable());
        log.info("Data Stored for visits");
    }

    public String hashable(){
        List<Visit> visits;
        visits = this.visitDAO.getAll(Datastores.SQLITE);
        String hashStringexpected = "";
        for(int i=0; i< 4; i++){
            Visit oldVists= visits.get(i);
            hashStringexpected = hashStringexpected + oldVists.getPetId() + oldVists.getDate() + oldVists.getDescription();

        }
        hashStringexpected = hashValue(hashStringexpected);
        return hashStringexpected;
    }

    public boolean hashConsistencyChecker(){
        String actual = hashable();
        dataChecker = this.visitDAO.getHash("visit");
        return  dataChecker.equals(actual);
    }

    private String hashValue(String value) {
        return DigestUtils.sha1Hex(value).toUpperCase();
    }

    public static LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

}
