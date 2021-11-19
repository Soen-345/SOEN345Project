package org.springframework.samples.petclinic.migration;

import org.springframework.samples.petclinic.vet.Vet;

import java.util.List;

public class VetMigration implements IMigration {


    private void initTable() {
        String deleteQuery = "DROP TABLE vets";
        QueryController.query(deleteQuery, Datastore.SQLITE, Table.VETS, false);

        String createQuery = "CREATE TABLE vets (\n" +
                "                      id         INTEGER IDENTITY PRIMARY KEY,\n" +
                "                      first_name VARCHAR(30),\n" +
                "                      last_name  VARCHAR(30)\n" +
                ");\n" +
                "CREATE INDEX vets_last_name ON vets (last_name);";

        QueryController.query(createQuery, Datastore.SQLITE, Table.VETS, false);
    }

    @Override
    public void forklift() {
        this.initTable();

        String selectQuery = "SELECT * FROM vets";
        List<Vet> vets = QueryController.query(selectQuery, Datastore.H2, Table.VETS, true);

        for (Vet vet : vets) {
            String insertQuery = "INSERT INTO vets (id, first_name, last_name) VALUES ('" + vet.getId() +
            "','" + vet.getFirstName() + "','" + vet.getLastName() + "');";
            QueryController.query(insertQuery, Datastore.SQLITE, Table.VETS, false);
        }

    }

    @Override
    public int checkConsistencies() {
        int inconsistencies = 0;
        String queryH2 = "SELECT * FROM vets";
        List<Vet> expected = QueryController.query(queryH2, Datastore.H2, Table.VETS, true);

        String querySQLite = "SELECT * FROM vets";
        List<Vet> actual = QueryController.query(querySQLite, Datastore.SQLITE, Table.VETS, true);

        for (int i = 0; i < actual.size(); i++) {
            if (!expected.get(i).equals(actual.get(i))) {
                inconsistencies++;
            }
        }
        return inconsistencies;
    }

    @Override
    public void logInconsistency() {

    }
}
