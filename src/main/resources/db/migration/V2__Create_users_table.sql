CREATE TABLE users (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    TEXT         NOT NULL,
    role        VARCHAR(50)  NOT NULL
);
