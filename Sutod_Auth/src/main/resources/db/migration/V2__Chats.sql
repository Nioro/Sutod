CREATE TABLE IF NOT EXISTS app_schema.chats(
    id SERIAL PRIMARY KEY,
    c_user_id BIGINT REFERENCES app_schema.users(id) ON DELETE SET NULL,
    c_user_id_2 BIGINT REFERENCES app_schema.users(id) ON DELETE SET NULL,

    CONSTRAINT uq_users_pair UNIQUE (c_user_id, c_user_id_2)
);