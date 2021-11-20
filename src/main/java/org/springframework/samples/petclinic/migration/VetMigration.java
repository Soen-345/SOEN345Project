package org.springframework.samples.petclinic.migration;

import org.springframework.samples.petclinic.vet.Vet;

import java.util.Map;
import java.util.Objects;

/**
 * @author Sevag Eordkian
 *
 * Shadow writes are not applicable for Vets, the system doesn't let users create a vet.
 */

public class VetMigration  {


    private static void initTable() {
        String deleteQuery = "DROP TABLE IF EXISTS vets";
        QueryController.queryVets(deleteQuery, Datastores.SQLITE, false);

        String createQuery = "CREATE TABLE vets (\n" +
                "                      id         INTEGER IDENTITY PRIMARY KEY,\n" +
                "                      first_name VARCHAR(30),\n" +
                "                      last_name  VARCHAR(30)\n" +
                ");\n" +
                "CREATE INDEX vets_last_name ON vets (last_name);";

        QueryController.queryVets(createQuery, Datastores.SQLITE, false);
    }


    public static void forklift(Map<Integer, Vet> vets) {

        if (!MigrationToggles.isUnderTest) {
            String selectQuery = "SELECT * FROM vets";
            vets = QueryController.queryVets(selectQuery, Datastores.H2, true);
        }

        initTable();
        for (Vet vet : vets.values()) {
            addVetToNewDatastore(vet);
        }
    }


    public static int checkConsistencies(Map<Integer, Vet> expected) {
        int inconsistencies = 0;

        if (!MigrationToggles.isUnderTest) {
            String queryH2 = "SELECT * FROM vets";
            expected = QueryController.queryVets(queryH2, Datastores.H2, true);
        }

        String querySQLite = "SELECT * FROM vets";
        Map<Integer, Vet> actual = QueryController.queryVets(querySQLite, Datastores.SQLITE, true);

        for (Integer key : expected.keySet()) {
            Vet exp = expected.get(key);
            Vet act = actual.get(key);
            if (act == null) {
                inconsistencies ++;

                addVetToNewDatastore(exp);
            }
            if (act != null && (!Objects.equals(exp.getId(), act.getId()) || !exp.getFirstName().equals(act.getFirstName()) ||
                    !exp.getLastName().equals(act.getLastName()))) {
                inconsistencies++;

                logInconsistency(exp, act);

                correctInconsistency(exp);
            }
        }
        return inconsistencies;
    }

    public static void correctInconsistency(Vet expected) {
        String query = "UPDATE vets SET first_name = '" + expected.getFirstName()
                + "', last_name = '" + expected.getLastName() + "' WHERE id = " + expected.getId() + ";";
        QueryController.queryVets(query, Datastores.SQLITE, false);

    }


    public static boolean shadowReadConsistencyChecker(Integer vetId) {
        Vet exp = getVetFromOldDatastore(vetId);
        Vet act = getVetFromNewDatastore(vetId);

        if (!Objects.equals(exp.getId(), act.getId()) || !exp.getFirstName().equals(act.getFirstName()) ||
                !exp.getLastName().equals(act.getLastName())) {

            logInconsistency(exp, act);

            correctInconsistency(exp);

            return false;
        }
        return true;

    }

    public static void addVetToNewDatastore(Vet vet) {
        String insertQuery = "INSERT INTO vets (id, first_name, last_name) VALUES (" + vet.getId() +
                ",'" + vet.getFirstName() + "','" + vet.getLastName() + "');";
        QueryController.queryVets(insertQuery, Datastores.SQLITE, false);
    }


    public static Vet getVetFromOldDatastore(Integer vetId) {
        String query = "SELECT id, first_name, last_name FROM vets WHERE id = " + vetId + ";";
        Map<Integer, Vet> result = QueryController.queryVets(query, Datastores.H2, true);

        return result.get(vetId);
    }

    public static Vet getVetFromNewDatastore(Integer vetId) {
        String query = "SELECT id, first_name, last_name FROM vets WHERE id = " + vetId + ";";
        Map<Integer, Vet> result = QueryController.queryVets(query, Datastores.SQLITE, true);

        return result.get(vetId);
    }

    public static void logInconsistency(Vet expected, Vet actual) {
        System.out.println("Vet Table Inconsistency - \n " +
                "Expected: " + expected.getId() + " " + expected.getFirstName() + " " + expected.getLastName() + "\n"
                + "Actual: " + actual.getId() + " " + actual.getFirstName() + " " + actual.getLastName());

    }
}
