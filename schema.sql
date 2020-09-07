CREATE SCHEMA STAGE;

USE STAGE;

-- -----------------------------------------------------
-- Code table that holds status of processing a record.
-- -----------------------------------------------------
CREATE TABLE STATUS (
    STATUS_CD CHAR(5) NOT NULL PRIMARY KEY,
    STATUS_DES VARCHAR(30) NOT NULL
);

-- -----------------------------------------------------
-- Stores the raw file that is being integrated into
-- the system.
-- -----------------------------------------------------
CREATE TABLE BATCH (
	BATCH_ID CHAR(36) NOT NULL PRIMARY KEY,       -- A unique ID for each batch.
	CREATE_TIME TIMESTAMP NOT NULL,               -- The time this record was created.
	PAYLOAD LONGTEXT                              -- The raw data that is being integrated.
);

-- -----------------------------------------------------
-- Stores an individual album that came in through the
-- the integration.
-- -----------------------------------------------------
CREATE TABLE ALBUM (

    ALBUM_ID CHAR(36) NOT NULL PRIMARY KEY,       -- A unique ID for this album in this batch.
    BATCH_ID CHAR(36) NOT NULL,                   -- The batch this record came from.
    CREATE_TIME TIMESTAMP NOT NULL,               -- The time this record was created.
    LAST_UPDATE_TIME TIMESTAMP NOT NULL,          -- The last time this record was updated.
    STATUS_CD CHAR(5) NOT NULL,                   -- The status of this album.
    GTIN_14 VARCHAR(1000) NOT NULL,                    -- The GTIN-14 (UPC) for this album.
    ALBUM_NAME VARCHAR(1000) NOT NULL,             -- The name of this album.
    ARTIST_NAME VARCHAR(1000) NOT NULL,            -- The name of this album.
    CONSTRAINT FK_ALBUM_BATCH FOREIGN KEY (BATCH_ID) REFERENCES BATCH(BATCH_ID)
);

-- -----------------------------------------------------
-- Sores the songs that are tied to an album.
-- -----------------------------------------------------
CREATE TABLE SONG (

    SONG_ID CHAR(36) NOT NULL PRIMARY KEY,        -- A unique ID for the song.
    ALBUM_ID CHAR(36) NOT NULL,                   -- The album the song is on.
    CREATE_TIME TIMESTAMP NOT NULL,               -- The time this record was created.
    SONG_NAME VARCHAR(1000) NOT NULL,              -- The name of the song.
    CONSTRAINT FK_SONG_ALBUM FOREIGN KEY (ALBUM_ID) REFERENCES ALBUM(ALBUM_ID)
);

-- -----------------------------------------------------
-- Stores any mapping errors that happened when trying
-- to store an album in the core system.
-- -----------------------------------------------------
CREATE TABLE ALBUM_ERROR (

    ERROR_ID CHAR(36) NOT NULL PRIMARY KEY,       -- A unique ID for this error.
    ALBUM_ID CHAR(36) NOT NULL,                   -- The album that was being processed when this error happened.
    BATCH_ID CHAR(36) NOT NULL,                   -- The batch this error occured in.
    CREATE_TIME TIMESTAMP NOT NULL,               -- The time this record was created.
    ERROR_TEXT VARCHAR(1000) NOT NULL,            -- The error.
    CONSTRAINT FK_ALBUM_ERROR FOREIGN KEY (ALBUM_ID) REFERENCES ALBUM(ALBUM_ID)
);

CREATE SCHEMA CORE;

USE CORE;

-- -----------------------------------------------------
-- The core album table that the integration will be
-- populating data into.
-- -----------------------------------------------------
CREATE TABLE CT_ALBUM (

    ALBUM_ID CHAR(36) NOT NULL PRIMARY KEY,       -- A unique ID for this album.
    GTIN_14 CHAR(14) NOT NULL,                    -- The GTIN-14 (UPC) for this album.
    ALBUM_NAME VARCHAR(100) NOT NULL,             -- The name of this album.
    ARTIST_NAME VARCHAR(100) NOT NULL,            -- The name of this album.
    CREATE_TIME TIMESTAMP NOT NULL,               -- The time this record was created.
    LAST_UPDATE_TIME TIMESTAMP NOT NULL,          -- The last time this record was updated.
    SOURCE_ALBUM_ID CHAR(36) NOT NULL,            -- The ALBUM_ID in the staging table that was the source of the
                                                  -- most recent information in this record.
    UNIQUE KEY UQ_GTIN_14 (GTIN_14)
);
