package org.springframework.samples.petclinic.owner;



import org.springframework.samples.petclinic.SqliteConnection;

import java.util.Collection;

public class PetDataMigration {

    // TODO - Beshoy
    public void forkliftPets() {

    }


    // TODO - Sevag
    public void forkliftTypes(Collection<PetType> petTypes) {

        for (PetType type : petTypes) {
            SqliteConnection.query("INSERT INTO types VALUES " + "(" + type.getId()
                    + "," + type.getName() + ")");
        }
    }

}
