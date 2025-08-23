CREATE TABLE IF NOT EXISTS app_schema.groups (
    id SERIAL PRIMARY KEY,
    c_group_name VARCHAR(100) NOT NULL,
    c_creator_id BIGINT REFERENCES app_schema.users(id) ON DELETE SET NULL,
    c_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    c_avatar VARCHAR(100)
);