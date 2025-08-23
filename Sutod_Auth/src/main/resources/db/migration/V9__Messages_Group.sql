CREATE TABLE IF NOT EXISTS app_schema.messages_groups(
    id SERIAL PRIMARY KEY,
    c_message_text VARCHAR NOT NULL,
    c_sender_id BIGINT REFERENCES app_schema.users(id) ON DELETE SET NULL,
    c_group_id BIGINT REFERENCES app_schema.groups(id) ON DELETE SET NULL,
    c_timestamp TIMESTAMP NOT NULL
);