CREATE TABLE users_postgres (
    id BIGSERIAL PRIMARY KEY,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(50),
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
