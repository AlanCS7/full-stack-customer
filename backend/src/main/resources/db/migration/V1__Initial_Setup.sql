CREATE TABLE customer
(
    id     SERIAL PRIMARY KEY,
    name   TEXT NOT NULL,
    email  TEXT NOT NULL UNIQUE,
    age    INT  NOT NULL,
    gender TEXT NOT NULL
);