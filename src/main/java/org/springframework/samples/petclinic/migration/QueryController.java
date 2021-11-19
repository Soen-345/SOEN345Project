package org.springframework.samples.petclinic.migration;

import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.vet.Specialty;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.visit.Visit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QueryController {

    protected static List query(String query, Datastore datastore, Table table, Boolean returnAnything) {
        List returnData = new ArrayList<>();

        if (table == Table.OWNERS) {
            returnData = queryOwners(query, datastore, returnAnything);
        }

        if (table == Table.PETS) {
            returnData = queryPets(query, datastore, returnAnything);
        }

        if (table == Table.VETS) {
            returnData = queryVets(query, datastore, returnAnything);
        }

        if (table == Table.SPECIALTIES) {
            returnData = querySpecialties(query, datastore, returnAnything);
        }

        if (table == Table.TYPES) {
            returnData = queryTypes(query, datastore, returnAnything);
        }

        if (table == Table.VET_SPECIALTIES) {
            returnData = queryVetSpecialties(query, datastore, returnAnything);
        }

        if (table == Table.VISITS) {
            returnData = queryVisits(query, datastore, returnAnything);
        }

        return returnData;
    }

    private static List<Visit> queryVisits(String query, Datastore datastore, Boolean returnAnything) {
        Connection conn = null;
        List<Visit> returnData = new ArrayList<>();

        if (datastore == Datastore.SQLITE) {
            conn = DatastoreConnection.connectSqlite();
        }
        if (datastore == Datastore.H2) {
            conn = DatastoreConnection.connectH2();
        }

        if (conn != null) {
            try {
                Statement statement = conn.createStatement();
                if (returnAnything) {
                    ResultSet resultSet = statement.executeQuery(query);
                    while (resultSet.next()) {
                        returnData.add(new Visit(resultSet.getInt("id"),
                                resultSet.getInt("pet_id"),
                                convertToLocalDateViaInstant(resultSet.getDate("visit_date")),
                                resultSet.getString("description")));
                    }
                } else {
                    statement.execute(query);
                    returnData = null;
                }


            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }

            }
        }
        return returnData;

    }

    private static List<Vet> queryVetSpecialties(String query, Datastore datastore, Boolean returnAnything) {
        Connection conn = null;
        List<Vet> returnData = new ArrayList<>();

        if (datastore == Datastore.SQLITE) {
            conn = DatastoreConnection.connectSqlite();
        }
        if (datastore == Datastore.H2) {
            conn = DatastoreConnection.connectH2();
        }

        if (conn != null) {
            try {
                Statement statement = conn.createStatement();
                if (returnAnything) {
                    ResultSet resultSet = statement.executeQuery(query);
                    while (resultSet.next()) {
                        String q = "SELECT id, name FROM specialty WHERE id = " + resultSet.getInt("specialty_id") + ";";
                        List<Specialty> specialty = querySpecialties(q, Datastore.H2, true);
                        returnData.add(new Vet(resultSet.getInt("vet_id"),
                                specialty.get(0)));
                    }
                } else {
                    statement.execute(query);
                    returnData = null;
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }

            }
        }
        return returnData;
    }

    private static List<PetType> queryTypes(String query, Datastore datastore, Boolean returnAnything) {
        Connection conn = null;
        List<PetType> returnData = new ArrayList<>();

        if (datastore == Datastore.SQLITE) {
            conn = DatastoreConnection.connectSqlite();
        }
        if (datastore == Datastore.H2) {
            conn = DatastoreConnection.connectH2();
        }

        if (conn != null) {
            try {
                Statement statement = conn.createStatement();
                if (returnAnything) {
                    ResultSet resultSet = statement.executeQuery(query);
                    while (resultSet.next()) {
                        returnData.add(new PetType(resultSet.getInt("id"),
                                resultSet.getString("name")));
                    }
                } else {
                    statement.execute(query);
                    returnData = null;
                }


            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }

            }
        }
        return returnData;
    }

    private static List<Vet> queryVets(String query, Datastore datastore, Boolean returnAnything) {
        Connection conn = null;
        List<Vet> returnData = new ArrayList<>();

        if (datastore == Datastore.SQLITE) {
            conn = DatastoreConnection.connectSqlite();
        }
        if (datastore == Datastore.H2) {
            conn = DatastoreConnection.connectH2();
        }

        if (conn != null) {
            try {
                Statement statement = conn.createStatement();
                if (returnAnything) {
                    ResultSet resultSet = statement.executeQuery(query);
                    while (resultSet.next()) {
                        returnData.add(new Vet(resultSet.getInt("id"),
                                resultSet.getString("first_name"),
                                resultSet.getString("last_name")));
                    }
                } else {
                    statement.execute(query);
                    returnData = null;
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }

            }
        }
        return returnData;
    }


    private static List<Owner> queryOwners(String query, Datastore datastore, Boolean returnAnything) {
        Connection conn = null;
        List<Owner> returnData = new ArrayList<>();

        if (datastore == Datastore.SQLITE) {
            conn = DatastoreConnection.connectSqlite();
        }
        if (datastore == Datastore.H2) {
            conn = DatastoreConnection.connectH2();
        }

        if (conn != null) {
            try {
                Statement statement = conn.createStatement();
                if (returnAnything) {
                    ResultSet resultSet = statement.executeQuery(query);
                    while (resultSet.next()) {
                        returnData.add(new Owner(resultSet.getInt("id"),
                                resultSet.getString("first_name"),
                                resultSet.getString("last_name"),
                                resultSet.getString("address"),
                                resultSet.getString("city"),
                                resultSet.getString("telephone")));
                    }
                } else {
                    statement.execute(query);
                    returnData = null;
                }


            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }

            }
        }
        return returnData;
    }

    private static List<Pet> queryPets(String query, Datastore datastore, Boolean returnAnything) {
        Connection conn = null;
        List<Pet> returnData = new ArrayList<>();

        if (datastore == Datastore.SQLITE) {
            conn = DatastoreConnection.connectSqlite();
        }
        if (datastore == Datastore.H2) {
            conn = DatastoreConnection.connectH2();
        }

        if (conn != null) {
            try {
                Statement statement = conn.createStatement();
                if (returnAnything) {
                    ResultSet resultSet = statement.executeQuery(query);
                    while (resultSet.next()) {
                        returnData.add(new Pet(resultSet.getInt("id"),
                                resultSet.getString("name"),
                                convertToLocalDateViaInstant(resultSet.getDate("birth_date")),
                                resultSet.getInt("type_id"),
                                resultSet.getInt("owner_id")));
                    }
                } else {
                    statement.execute(query);
                    returnData = null;
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }

            }
        }
        return returnData;
    }

    private static List<Specialty> querySpecialties(String query, Datastore datastore, Boolean returnAnything) {
        Connection conn = null;
        List<Specialty> returnData = new ArrayList<>();

        if (datastore == Datastore.SQLITE) {
            conn = DatastoreConnection.connectSqlite();
        }
        if (datastore == Datastore.H2) {
            conn = DatastoreConnection.connectH2();
        }

        if (conn != null) {
            try {
                Statement statement = conn.createStatement();
                if (returnAnything) {
                    ResultSet resultSet = statement.executeQuery(query);
                    while (resultSet.next()) {
                        returnData.add(new Specialty(resultSet.getString("name"),
                                resultSet.getInt("id")));
                    }
                } else {
                    statement.execute(query);
                    returnData = null;
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }

            }
        }
        return returnData;
    }


    public static LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

}
