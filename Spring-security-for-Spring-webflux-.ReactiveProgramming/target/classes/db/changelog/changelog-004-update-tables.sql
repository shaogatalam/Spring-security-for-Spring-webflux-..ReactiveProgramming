-- liquibase formatted sql

-- changeset ShaogatAlam:004
ALTER TABLE users_postgres ADD COLUMN phone_number VARCHAR(15);

-- changeset ShaogatAlam:005
ALTER TABLE users_postgres DROP COLUMN phone_number;

-- changeset ShaogatAlam:006
ALTER TABLE users_postgres ADD COLUMN phone_number VARCHAR(15);

-- changeset ShaogatAlam:007
ALTER TABLE users_postgres ADD COLUMN city VARCHAR(55);

-- changeset ShaogatAlam:008
ALTER TABLE users_postgres DROP COLUMN city;