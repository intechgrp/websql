# --- database schema

# --- !Ups

CREATE TABLE CLIENT (
    ID                  BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    NOM                 VARCHAR(255)    NOT NULL,
    PRENOM              VARCHAR(255)    NOT NULL DEFAULT '',
    ADRESSE             VARCHAR(255)    NOT NULL DEFAULT ''
);

CREATE TABLE COMPTE (
    ID                  BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    DESCRIPTION         VARCHAR(255) NOT NULL DEFAULT '',
    SOLDE               DOUBLE NOT NULL DEFAULT 0,
    DEVISE              VARCHAR(3) NOT NULL DEFAULT 'EUR'
);

# --- !Downs

drop table if exists CLIENT;
drop table if exists COMPTE;
