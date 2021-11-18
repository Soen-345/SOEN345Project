DROP TABLE vet_specialties IF EXISTS;
DROP TABLE vets IF EXISTS;
DROP TABLE specialties IF EXISTS;
DROP TABLE visits IF EXISTS;
DROP TABLE pets IF EXISTS;
DROP TABLE types IF EXISTS;
DROP TABLE owners IF EXISTS;


CREATE TABLE vets (
                      id         INTEGER IDENTITY PRIMARY KEY,
                      first_name VARCHAR(30),
                      last_name  VARCHAR(30)
);
CREATE INDEX vets_last_name ON vets (last_name);

CREATE TABLE specialties (
                             id   INTEGER IDENTITY PRIMARY KEY,
                             name VARCHAR(80)
);
CREATE INDEX specialties_name ON specialties (name);

CREATE TABLE vet_specialties (
                                 vet_id       INTEGER NOT NULL,
                                 specialty_id INTEGER NOT NULL
);

CREATE TABLE types (
                       id   INTEGER IDENTITY PRIMARY KEY,
                       name VARCHAR(80)
);
CREATE INDEX types_name ON types (name);

CREATE TABLE owners (
                        id         INTEGER IDENTITY PRIMARY KEY,
                        first_name VARCHAR(30),
                        last_name  VARCHAR_IGNORECASE(30),
                        address    VARCHAR(255),
                        city       VARCHAR(80),
                        telephone  VARCHAR(20)
);
CREATE INDEX owners_last_name ON owners (last_name);

CREATE TABLE pets (
                      id         INTEGER IDENTITY PRIMARY KEY,
                      name       VARCHAR(30),
                      birth_date DATE,
                      type_id    INTEGER NOT NULL,
                      owner_id   INTEGER NOT NULL
);
CREATE INDEX pets_name ON pets (name);

CREATE TABLE visits (
                        id          INTEGER IDENTITY PRIMARY KEY,
                        pet_id      INTEGER NOT NULL,
                        visit_date  DATE,
                        description VARCHAR(255)
);
CREATE INDEX visits_pet_id ON visits (pet_id);
