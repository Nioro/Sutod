CREATE TABLE IF NOT EXISTS app_schema.messages(
    id SERIAL PRIMARY KEY,
    c_message_text VARCHAR NOT NULL,
    c_sender_id BIGINT REFERENCES app_schema.users(id) ON DELETE SET NULL,
    c_chat_id BIGINT REFERENCES app_schema.chats(id) ON DELETE SET NULL,
    c_timestamp TIMESTAMP NOT NULL
);