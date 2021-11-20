package org.springframework.samples.petclinic.migration;

import org.springframework.samples.petclinic.vet.Vet;

import java.util.List;
import java.util.Map;

public class VetMigration  {


    private void initTable() {
        String deleteQuery = "DROP TABLE IF EXISTS vets";
        QueryController.queryVets(deleteQuery, Datastore.SQLITE, false);

        String createQuery = "CREATE TABLE vets (\n" +
                "                      id         INTEGER IDENTITY PRIMARY KEY,\n" +
                "                      first_name VARCHAR(30),\n" +
                "                      last_name  VARCHAR(30)\n" +
                ");\n" +
                "CREATE INDEX vets_last_name ON vets (last_name);";

        QueryController.queryVets(createQuery, Datastore.SQLITE, false);
    }


    public void forklift(Map<Integer, Vet> vets) {

        if (!DatastoreToggles.isUnderTest) {
            String selectQuery = "SELECT * FROM vets";
            vets = QueryController.queryVets(selectQuery, Datastore.H2, true);
        }

        this.initTable();
        for (Vet vet : vets.values()) {
            String insertQuery = "INSERT INTO vets (id, first_name, last_name) VALUES ('" + vet.getId() +
            "','" + vet.getFirstName() + "','" + vet.getLastName() + "');";
            QueryController.queryVets(insertQuery, Datastore.SQLITE, false);
        }
    }



    public int checkConsistencies(Map<Integer, Vet> expected) {
        int inconsistencies = 0;

        if (!DatastoreToggles.isUnderTest) {
            String queryH2 = "SELECT * FROM vets";
            expected = QueryController.queryVets(queryH2, Datastore.H2, true);
        }

        String querySQLite = "SELECT * FROM vets";
        Map<Integer, Vet> actual = QueryController.queryVets(querySQLite, Datastore.SQLITE, true);

        for (Integer key : expected.keySet()) {
            Vet exp = expected.get(key);
            Vet act = actual.get(key);
            if (exp.getId() != act.getId() || !exp.getFirstName().equals(act.getFirstName()) ||
                    !exp.getLastName().equals(act.getLastName())) {
                inconsistencies++;
                System.out.println(exp + " ====== " + act);
                this.logInconsistency(exp.getId(), act.getId());
            }
        }
        return inconsistencies;
    }


    public void logInconsistency(Integer expected, Integer actual) {
        System.out.println("Vet Table Incosistency - \n " +
                "Expected: " + expected + "\n"
                + "Actual: " + actual);

    }
}
