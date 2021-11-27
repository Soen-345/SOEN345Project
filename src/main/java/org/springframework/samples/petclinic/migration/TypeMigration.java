package org.springframework.samples.petclinic.migration;

import org.springframework.samples.petclinic.owner.PetType;

import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

public class TypeMigration implements IMigration <PetType> {
    private final TypeDAO typeDAO;

    public TypeMigration() {
        typeDAO = new TypeDAO();
    }

    public int forklift() {
        this.typeDAO.initTable();
        int numInsert = 0;

        Map<Integer, PetType> types = this.typeDAO.getAllTypes(Datastores.H2);

        for (PetType type : types.values()) {
            boolean success = this.typeDAO.addType(type, Datastores.SQLITE);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }

    public int forkliftTestOnly(Map<Integer, PetType> types) {

        this.typeDAO.initTable();
        int numInsert = 0;

        for (PetType type : types.values()) {
            boolean success = this.typeDAO.addType(type, Datastores.SQLITE);
            if (success) {
                numInsert++;
            }
        }
        return numInsert;
    }


    public int checkConsistencies() {

        int inconsistencies = 0;


        Map<Integer, PetType> expected = this.typeDAO.getAllTypes(Datastores.H2);

        Map<Integer, PetType> actual = this.typeDAO.getAllTypes(Datastores.SQLITE);

        for (Integer key : expected.keySet()) {
            PetType exp = expected.get(key);
            PetType act = actual.get(key);
            if (act == null) {
                inconsistencies++;
                logInconsistency(exp, null);
                this.typeDAO.addType(exp, Datastores.SQLITE);
            }
            if (act != null && (!Objects.equals(exp.getId(), act.getId()) || !exp.getName().equals(act.getName()) )) {
                inconsistencies++;

                logInconsistency(exp, act);

                this.typeDAO.update(exp, Datastores.SQLITE);
            }
        }

        return inconsistencies;
    }

    public int checkConsistenciesTestOnly(Map<Integer, PetType> expected) {

        int inconsistencies = 0;

        Map<Integer, PetType> actual = this.typeDAO.getAllTypes(Datastores.SQLITE);

        for (Integer key : expected.keySet()) {
            PetType exp = expected.get(key);
            PetType act = actual.get(key);
            if (act == null) {
                inconsistencies++;
                logInconsistency(exp, null);
                this.typeDAO.addType(exp, Datastores.SQLITE);
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

        PetType act = this.typeDAO.getType(exp.getId(), Datastores.SQLITE);

        if (act == null) {
            this.typeDAO.addType(exp, Datastores.SQLITE);

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

    public void logInconsistency(PetType expected, PetType actual) {

        if (actual == null) {
            System.out.println("Types Table Inconsistency - \n " +
                    "Expected: " + expected.getId() + " " +   expected.getName() + "\n"
                    + "Actual: NULL");
        } else {
            System.out.println("Types Table Inconsistency - \n " +
                    "Expected: " + expected.getId() + " "  + expected.getName() + "\n"
                    + "Actual: " + actual.getId() + " "  + actual.getName());
        }
    }

    public void shadowWrite(PetType type) {
        this.typeDAO.addType(type, Datastores.SQLITE);
    }

    public void closeConnections() throws SQLException {
        this.typeDAO.closeConnections();
    }
}
