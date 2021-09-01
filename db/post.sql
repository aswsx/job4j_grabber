DROP TABLE IF EXISTS grabber;
CREATE TABLE IF NOT EXISTS grabber (
    id serial primary key,
    name text UNIQUE,
    text text,
    link text,
    created timestamp
);