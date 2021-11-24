package org.springframework.samples.petclinic.owner;

public class OwnerToggles {

    // The following two booleans are for the new feature
    public static boolean isSearchFirstNameEnabled=true;
    public static boolean isSearchLastNameEnabled=false;


    // toggle for existing feature
    // this toggle is for the adding owner button feature which is located in the "Find Owner" tab of the webpage
    public static Boolean isAddOwnerButtonEnabled = true;
}
