package org.springframework.samples.petclinic.migration;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.samples.petclinic.owner.PetType;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TypeMigrationTest {
    private static TypeMigration typeMigration;

    private static Map<Integer, PetType> oldDataStoreTypes;

    static PetType type1;
    static PetType type2;
    static PetType type3;
    static PetType type4;
    static PetType type5;

    @BeforeAll
    public static void setup() {

        MigrationToggles.isUnderTest = true;

        typeMigration = new TypeMigration();

        type1 = Mockito.mock(PetType.class);
        type2 = Mockito.mock(PetType.class);
        type3 = Mockito.mock(PetType.class);
        type4 = Mockito.mock(PetType.class);
        type5 = Mockito.mock(PetType.class);

        when(type1.getId()).thenReturn(1);
        when(type1.getName()).thenReturn("cat");

        when(type2.getId()).thenReturn(2);
        when(type2.getName()).thenReturn("dog");

        when(type3.getId()).thenReturn(3);
        when(type3.getName()).thenReturn("lizard");

        when(type4.getId()).thenReturn(4);
        when(type4.getName()).thenReturn("snake");

        when(type5.getId()).thenReturn(5);
        when(type5.getName()).thenReturn("bird");




        oldDataStoreTypes = new HashMap<>();

        oldDataStoreTypes.put(type1.getId(), type1);
        oldDataStoreTypes.put(type2.getId(), type2);
        oldDataStoreTypes.put(type3.getId(), type3);

    }

    @Test()
    @Order(1)
    public void testForklift() {

        assertEquals(3, typeMigration.forkliftTestOnly(oldDataStoreTypes));

    }

    @Test
    @Order(2)
    public void testCheckConsistency() {

        oldDataStoreTypes.put(type4.getId(), type4);


        assertEquals(1, typeMigration.checkConsistenciesTestOnly(oldDataStoreTypes));
    }

    @Test
    @Order(3)
    public void testShadowReadConsistencyChecker() {

        oldDataStoreTypes.put(type5.getId(), type5);


        typeMigration.shadowWrite(type5);


        assertTrue(typeMigration.shadowReadWriteConsistencyChecker(type5));


    }

    @AfterAll
    public static void closeConnection() throws SQLException {
        typeMigration.closeConnections();
    }


}
