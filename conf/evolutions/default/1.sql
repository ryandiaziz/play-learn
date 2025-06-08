# --- !Ups
CREATE TABLE users
(
    id    SERIAL PRIMARY KEY,
    name  VARCHAR(255),
    email VARCHAR(255)
);

# --- !Downs
DROP TABLE users;