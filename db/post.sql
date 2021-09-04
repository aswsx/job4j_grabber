DROP TABLE IF EXISTS post;
CREATE TABLE IF NOT EXISTS post (
    id serial primary key,
    name text,
    text text,
    link text UNIQUE,
    created timestamp
);