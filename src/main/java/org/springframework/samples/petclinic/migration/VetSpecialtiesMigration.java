package org.springframework.samples.petclinic.migration;

import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.vet.VetSpecialties;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author Alireza Ziarizi
 */
public class VetSpecialtiesMigration implements IMigration {
    private final VetSpecialtiesDAO vetSpecialtiesDAO;

    public VetSpecialtiesMigration() {
        vetSpecialtiesDAO = new VetSpecialtiesDAO();
    }

    @Override
    public int forklift() {
        this.vetSpecialtiesDAO.initTable();
        int numInsert = 0;

        Map<Integer, VetSpecialties> vetSpecialties = this.vetSpecialtiesDAO.getAllVetSpecialties(Datastores.H2);

        for(VetSpecialties vetSpecialtie : vetSpecialties.values()){
            boolean success = this.vetSpecialtiesDAO.addVetSpecialties(vetSpecialtie,Datastores.SQLITE);
            if(success){
                numInsert++;
            }
        }

        return numInsert;
    }
    public int forkliftTestOnly(Map<Integer, VetSpecialties> VetSpecialties){
        this.vetSpecialtiesDAO.initTable();
        int numInsert = 0;
        for(VetSpecialties vetSpecialtie : VetSpecialties.values()){
            boolean success = this.vetSpecialtiesDAO.addVetSpecialties(vetSpecialtie,Datastores.SQLITE);
            if(success){
                numInsert++;
            }
        }
        return numInsert;
    }

    @Override
    public int checkConsistencies() {
        return 0;
    }


    @Override
    public boolean shadowReadWriteConsistencyChecker(Object o) {
        return false;
    }

    @Override
    public void logInconsistency(Object expected, Object actual) {

    }

    @Override
    public void closeConnections() throws SQLException {

    }
}
