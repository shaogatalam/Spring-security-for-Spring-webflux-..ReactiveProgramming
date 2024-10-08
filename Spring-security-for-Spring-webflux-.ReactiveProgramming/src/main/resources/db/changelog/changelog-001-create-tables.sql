-- liquibase formatted sql

-- changeset Shaogat alam:001
CREATE TABLE IF NOT EXISTS users_pg (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255)
);


CREATE TABLE IF NOT EXISTS users_100 (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255)
);