package org.springframework.samples.petclinic.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.vet.VetSpecialty;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author Alireza Ziarizi
 */
public class VetSpecialtiesMigration  {

    private static final Logger log = LoggerFactory.getLogger(VisitMigration.class);

    private final VetSpecialtiesDAO vetSpecialtiesDAO;

    public VetSpecialtiesMigration() {
        vetSpecialtiesDAO = new VetSpecialtiesDAO();
    }

    public int forklift() {
        this.vetSpecialtiesDAO.initTable();
        int numInsert = 0;

        List<VetSpecialty> vetSpecialties = this.vetSpecialtiesDAO.getAll(Datastores.H2);

        for(VetSpecialty vetSpecialty : vetSpecialties){
            boolean success = this.vetSpecialtiesDAO.migrate(vetSpecialty);
            if(success){
                numInsert++;
            }
        }

        return numInsert;
    }
    public int forkliftTestOnly(List<VetSpecialty> VetSpecialties){
        this.vetSpecialtiesDAO.initTable();
        int numInsert = 0;

        for(VetSpecialty vetSpecialty : VetSpecialties){
            boolean success = this.vetSpecialtiesDAO.migrate(vetSpecialty);
            if(success){
                numInsert++;
            }
        }
        return numInsert;
    }


    public int checkConsistencies() {
        int inconsistencies = 0;

        List<VetSpecialty> expected = this.vetSpecialtiesDAO.getAll(Datastores.H2);

        for(VetSpecialty exp : expected){
            VetSpecialty act = this.vetSpecialtiesDAO.get(exp, Datastores.SQLITE);

            if(act == null){
                inconsistencies++;
                logInconsistency(exp, null);
                this.vetSpecialtiesDAO.add(exp, Datastores.SQLITE);
            }
        }
        return inconsistencies;
    }

    public int checkConsistenciesTestOnly(List<VetSpecialty> expected) {

        int inconsistencies = 0;

        for(VetSpecialty exp : expected){
            VetSpecialty act = this.vetSpecialtiesDAO.get(exp, Datastores.SQLITE);

            if(act == null){
                inconsistencies++;
                logInconsistency(exp, null);
                this.vetSpecialtiesDAO.add(exp, Datastores.SQLITE);
            }
        }
        return inconsistencies;
    }


    public void logInconsistency(VetSpecialty expected, VetSpecialty actual) {

        if (actual == null) {
            log.warn("Vet Specialty Table Inconsistency - \n " +
                    "Expected: " + expected.getVet_id() + " " + expected.getSpecialty_id() + "\n" +
                    "Actual: NULL");
        } else {
            log.warn("Vet Specialty Table Inconsistency - \n " +
                    "Expected: " + expected.getVet_id() + " " + expected.getSpecialty_id() + "\n"
                    + "Actual: " + actual.getVet_id() + " " + actual.getSpecialty_id());
        }

    }

    public void closeConnections() throws SQLException {
        vetSpecialtiesDAO.closeConnections();
    }
}
