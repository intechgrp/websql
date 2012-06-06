# --- database schema

# --- !Ups

CREATE TABLE CLIENT (
    ID                  BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    NOM                 VARCHAR(255)    NOT NULL,
    PRENOM              VARCHAR(255)    NOT NULL DEFAULT '',
    ADRESSE             VARCHAR(255)    NOT NULL DEFAULT '',
    LOGIN               VARCHAR(50)     NOT NULL DEFAULT ''
);

CREATE TABLE COMPTE (
    ID                  BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    IBAN                VARCHAR(30) NOT NULL,
    DESCRIPTION         VARCHAR(255) NOT NULL DEFAULT '',
    SOLDE               DOUBLE NOT NULL DEFAULT 0,
    DEVISE              VARCHAR(3) NOT NULL DEFAULT 'EUR',
    CLIENT              BIGINT NOT NULL,
    FOREIGN KEY(CLIENT) REFERENCES CLIENT(ID)
);

CREATE TABLE USER(
    LOGIN               VARCHAR(50) NOT NULL PRIMARY KEY,
    PASSWORD            VARCHAR(50) NOT NULL,
    USERNAME            VARCHAR(100) NOT NULL DEFAULT ''
)

# --- !Downs

drop table if exists CLIENT;
drop table if exists COMPTE;
