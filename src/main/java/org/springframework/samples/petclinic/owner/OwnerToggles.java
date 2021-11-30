package org.springframework.samples.petclinic.owner;

import java.util.concurrent.ThreadLocalRandom;

public class OwnerToggles {

    // The following two booleans are for the new feature
    public static boolean isSearchFirstNameEnabled=true;
    public static boolean isSearchLastNameEnabled=false;


    // toggle for existing feature
    // this toggle is for the adding owner button feature which is located in the "Find Owner" tab of the webpage
    public static Boolean isAddOwnerButtonEnabled = true;
    // toggle does not allow the owner information and its pet information to be updated
    public static Boolean isUpdateOwnerEnabled = true;


    public static Boolean assignSearchNameFeature(int proportion){
        int rnd= ThreadLocalRandom.current().nextInt(1,101);
        if(proportion>rnd){
            isSearchFirstNameEnabled=true;
            isSearchLastNameEnabled=false;
            return true;
        }
        isSearchFirstNameEnabled=false;
        isSearchLastNameEnabled=true;

        return false;
    }
}
