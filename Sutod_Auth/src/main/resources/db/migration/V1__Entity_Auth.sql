CREATE SCHEMA IF NOT EXISTS app_schema;

CREATE TABLE IF NOT EXISTS app_schema.users(
    id SERIAL PRIMARY KEY,
    c_username VARCHAR(50) NOT NULL,
    c_email VARCHAR(50) NOT NULL,
    c_password VARCHAR(30) NOT NULL,
    c_is_online BOOLEAN NOT NULL,
    c_avatar VARCHAR(100)
)