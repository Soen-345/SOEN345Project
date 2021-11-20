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
import java.util.*;

public class QueryController {


    protected static Map<Integer, Visit> queryVisits(String query, Datastores datastore, Boolean returnAnything) {
        Connection conn = null;
        Map<Integer, Visit> returnData = new HashMap<>();

        if (datastore == Datastores.SQLITE) {
            conn = DatastoreConnection.connectSqlite();
        }
        if (datastore == Datastores.H2) {
            conn = DatastoreConnection.connectH2();
        }

        if (conn != null) {
            try {
                Statement statement = conn.createStatement();
                if (returnAnything) {
                    ResultSet resultSet = statement.executeQuery(query);
                    while (resultSet.next()) {
                        returnData.put(resultSet.getInt("id"),
                                new Visit(resultSet.getInt("id"),
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

    protected static Map<Integer, Vet> queryVetSpecialties(String query, Datastores datastore, Boolean returnAnything) {
        Connection conn = null;
        Map<Integer, Vet> returnData = new HashMap<>();

        if (datastore == Datastores.SQLITE) {
            conn = DatastoreConnection.connectSqlite();
        }
        if (datastore == Datastores.H2) {
            conn = DatastoreConnection.connectH2();
        }

        if (conn != null) {
            try {
                Statement statement = conn.createStatement();
                if (returnAnything) {
                    ResultSet resultSet = statement.executeQuery(query);
                    while (resultSet.next()) {
                        String q = "SELECT id, name FROM specialty WHERE id = " + resultSet.getInt("specialty_id") + ";";
                        Map<Integer, Specialty> specialty = querySpecialties(q, Datastores.H2, true);
                        returnData.put(resultSet.getInt("id"),
                                new Vet(resultSet.getInt("vet_id"),
                                specialty.get(resultSet.getInt("specialty_id"))));
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

    protected static Map<Integer, PetType> queryTypes(String query, Datastores datastore, Boolean returnAnything) {
        Connection conn = null;
        Map<Integer, PetType> returnData = new HashMap<>();

        if (datastore == Datastores.SQLITE) {
            conn = DatastoreConnection.connectSqlite();
        }
        if (datastore == Datastores.H2) {
            conn = DatastoreConnection.connectH2();
        }

        if (conn != null) {
            try {
                Statement statement = conn.createStatement();
                if (returnAnything) {
                    ResultSet resultSet = statement.executeQuery(query);
                    while (resultSet.next()) {
                        returnData.put(resultSet.getInt("id"),
                                new PetType(resultSet.getInt("id"),
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

    protected static Map<Integer, Vet> queryVets(String query, Datastores datastore, Boolean returnAnything) {
        Connection conn = null;
        Map<Integer, Vet> returnData = new HashMap<>();

        if (datastore == Datastores.SQLITE) {
            conn = DatastoreConnection.connectSqlite();
        }
        if (datastore == Datastores.H2) {
            conn = DatastoreConnection.connectH2();
        }

        if (conn != null) {
            try {
                Statement statement = conn.createStatement();
                if (returnAnything) {
                    ResultSet resultSet = statement.executeQuery(query);
                    while (resultSet.next()) {
                        returnData.put(resultSet.getInt("id"),
                                new Vet(resultSet.getInt("id"),
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


    protected static Map<Integer, Owner> queryOwners(String query, Datastores datastore, Boolean returnAnything) {
        Connection conn = null;
        Map<Integer, Owner> returnData = new HashMap<>();

        if (datastore == Datastores.SQLITE) {
            conn = DatastoreConnection.connectSqlite();
        }
        if (datastore == Datastores.H2) {
            conn = DatastoreConnection.connectH2();
        }

        if (conn != null) {
            try {
                Statement statement = conn.createStatement();
                if (returnAnything) {
                    ResultSet resultSet = statement.executeQuery(query);
                    while (resultSet.next()) {
                        returnData.put(resultSet.getInt("id"),
                                new Owner(resultSet.getInt("id"),
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

    protected static Map<Integer, Pet> queryPets(String query, Datastores datastore, Boolean returnAnything) {
        Connection conn = null;
        Map<Integer, Pet> returnData = new HashMap<>();

        if (datastore == Datastores.SQLITE) {
            conn = DatastoreConnection.connectSqlite();
        }
        if (datastore == Datastores.H2) {
            conn = DatastoreConnection.connectH2();
        }

        if (conn != null) {
            try {
                Statement statement = conn.createStatement();
                if (returnAnything) {
                    ResultSet resultSet = statement.executeQuery(query);
                    while (resultSet.next()) {
                        returnData.put(resultSet.getInt("id"),
                                new Pet(resultSet.getInt("id"),
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

    protected static Map<Integer, Specialty> querySpecialties(String query, Datastores datastore, Boolean returnAnything) {
        Connection conn = null;
        Map<Integer, Specialty> returnData = new HashMap<>();

        if (datastore == Datastores.SQLITE) {
            conn = DatastoreConnection.connectSqlite();
        }
        if (datastore == Datastores.H2) {
            conn = DatastoreConnection.connectH2();
        }

        if (conn != null) {
            try {
                Statement statement = conn.createStatement();
                if (returnAnything) {
                    ResultSet resultSet = statement.executeQuery(query);
                    while (resultSet.next()) {
                        returnData.put(resultSet.getInt("id"),
                                new Specialty(resultSet.getString("name"),
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


    private static LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

}
