CREATE SCHEMA STAGE;

SET SCHEMA 'STAGE';

CREATE TABLE STATUS (
                        STATUS_CD CHAR(5) NOT NULL PRIMARY KEY,
                        STATUS_DES VARCHAR(30) NOT NULL
);

CREATE TABLE BATCH (
                       BATCH_ID CHAR(36) NOT NULL PRIMARY KEY,
                       CREATE_TIME TIMESTAMP NOT NULL,
                       PAYLOAD CLOB
);

CREATE TABLE ALBUM (

                       ALBUM_ID CHAR(36) NOT NULL PRIMARY KEY,
                       BATCH_ID CHAR(36) NOT NULL,
                       CREATE_TIME TIMESTAMP NOT NULL,
                       LAST_UPDATE_TIME TIMESTAMP NOT NULL,
                       STATUS_CD CHAR(5) NOT NULL,
                       GTIN_14 VARCHAR(1000) NOT NULL,
                       ALBUM_NAME VARCHAR(1000) NOT NULL,
                       ARTIST_NAME VARCHAR(1000) NOT NULL,
                       CONSTRAINT FK_ALBUM_BATCH FOREIGN KEY (BATCH_ID) REFERENCES BATCH(BATCH_ID)
);

CREATE TABLE SONG (

                      SONG_ID CHAR(36) NOT NULL PRIMARY KEY,
                      ALBUM_ID CHAR(36) NOT NULL,
                      CREATE_TIME TIMESTAMP NOT NULL,
                      SONG_NAME VARCHAR(1000) NOT NULL,
                      CONSTRAINT FK_SONG_ALBUM FOREIGN KEY (ALBUM_ID) REFERENCES ALBUM(ALBUM_ID)
);

CREATE TABLE ALBUM_ERROR (

                             ERROR_ID CHAR(36) NOT NULL PRIMARY KEY,
                             ALBUM_ID CHAR(36) NOT NULL,
                             BATCH_ID CHAR(36) NOT NULL,
                             CREATE_TIME TIMESTAMP NOT NULL,
                             ERROR_TEXT VARCHAR(1000) NOT NULL,
                             CONSTRAINT FK_ALBUM_ERROR FOREIGN KEY (ALBUM_ID) REFERENCES ALBUM(ALBUM_ID)
);

CREATE SCHEMA CORE;

SET SCHEMA 'CORE';

CREATE TABLE CT_ALBUM (

                          ALBUM_ID CHAR(36) NOT NULL PRIMARY KEY,
                          GTIN_14 CHAR(14) NOT NULL,
                          ALBUM_NAME VARCHAR(100) NOT NULL,
                          ARTIST_NAME VARCHAR(100) NOT NULL,
                          CREATE_TIME TIMESTAMP NOT NULL,
                          LAST_UPDATE_TIME TIMESTAMP NOT NULL,
                          SOURCE_ALBUM_ID CHAR(36) NOT NULL,
                          UNIQUE KEY UQ_GTIN_14 (GTIN_14)
);
