CREATE SCHEMA SRR;

SET SCHEMA 'SRR';

CREATE TABLE SRR_TEST (
    ID NUMBER NOT NULL PRIMARY KEY,
    VALUE VARCHAR(30) NOT NULL
);