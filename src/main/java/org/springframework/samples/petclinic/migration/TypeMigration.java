package org.springframework.samples.petclinic.migration;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class TypeMigration implements IMigration<PetType> {

    private static final Logger log = LoggerFactory.getLogger(TypeMigration.class);
    private final TypeDAO typeDAO;
    private String dataChecker= "";
    public TypeMigration() {
        typeDAO = new TypeDAO();
    }

    public int forklift() {

        this.typeDAO.initTable();
        int numInsert = 0;

        List<PetType> types = this.typeDAO.getAll(Datastores.H2);

        for (PetType type : types) {
            boolean success = this.typeDAO.migrate(type);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }

    public int forkliftTestOnly(List<PetType> types) {

        this.typeDAO.initTable();
        int numInsert = 0;

        for (PetType type : types) {
            boolean success = this.typeDAO.migrate(type);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }


    public int checkConsistencies() {

        int inconsistencies = 0;


        List<PetType> expected = this.typeDAO.getAll(Datastores.H2);

        for (PetType exp : expected) {
            PetType act = this.typeDAO.get(exp.getId(), Datastores.SQLITE);
            if (act == null) {
                inconsistencies++;
                logInconsistency(exp, null);
                this.typeDAO.add(exp, Datastores.SQLITE);
            }
            if (act != null && (!Objects.equals(exp.getId(), act.getId()) || !exp.getName().equals(act.getName()) )) {
                inconsistencies++;

                logInconsistency(exp, act);

                this.typeDAO.update(exp, Datastores.SQLITE);
            }
        }

        return inconsistencies;
    }

    public int checkConsistenciesTestOnly(List<PetType> expected) {

        int inconsistencies = 0;

        for (PetType exp : expected) {
            PetType act = this.typeDAO.get(exp.getId(), Datastores.SQLITE);
            if (act == null) {
                inconsistencies++;
                logInconsistency(exp, null);
                this.shadowWrite(exp);
            }
            if (act != null && (!Objects.equals(exp.getId(), act.getId()) || !exp.getName().equals(act.getName()) )) {
                inconsistencies++;

                logInconsistency(exp, act);

                this.typeDAO.update(exp, Datastores.SQLITE);
            }
        }

        return inconsistencies;
    }

    public boolean shadowReadWriteConsistencyChecker(PetType exp) {

        PetType act = this.typeDAO.get(exp.getId(), Datastores.SQLITE);

        if (act == null) {
            this.typeDAO.add(exp, Datastores.SQLITE);

            logInconsistency(exp, null);

            return false;
        }

        if (!exp.getId().equals(act.getId()) || !exp.getName().equals(act.getName())) {

            logInconsistency(exp, act);

            this.typeDAO.update(exp, Datastores.SQLITE);

            return false;
        }

        return true;
    }

    public List<PetType> findTypes() {
        return this.typeDAO.getAll(Datastores.SQLITE);
    }

    public void logInconsistency(PetType expected, PetType actual) {

        if (actual == null) {
            log.warn("Types Table Inconsistency - \n " +
                    "Expected: " + expected.getId() + " " +   expected.getName() + "\n"
                    + "Actual: NULL");
        } else {
            log.warn("Types Table Inconsistency - \n " +
                    "Expected: " + expected.getId() + " "  + expected.getName() + "\n"
                    + "Actual: " + actual.getId() + " "  + actual.getName());
        }
    }

    public void shadowWrite(PetType type) {
        if (MigrationToggles.isUnderTest) {
            this.typeDAO.migrate(type);
        }
        else {
            this.typeDAO.add(type, Datastores.SQLITE);
        }
    }

    public void updateData(){
        this.typeDAO.addHashStorage("pettype",hashable());
        log.info("hashtable created for type");
    }
    public String hashable(){
        List<PetType> petType;
        petType = this.typeDAO.getAll(Datastores.SQLITE);
        String hashStringexpected = "";
        for(int i=0; i< 4; i++){
            PetType oldpetType= petType.get(i);
            hashStringexpected = hashStringexpected + oldpetType.getName() + oldpetType.getId();

        }
        hashStringexpected = hashValue(hashStringexpected);
        return hashStringexpected;
    }
    public boolean hashConsistencyChecker(){
        String actual = hashable();
        dataChecker = this.typeDAO.getHash("pettype");
        return  dataChecker.equals(actual);
    }
    private String hashValue(String value) {
        return DigestUtils.sha1Hex(value).toUpperCase();
    }

    public void closeConnections() throws SQLException {
        this.typeDAO.closeConnections();
    }
}

