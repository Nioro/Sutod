CREATE TABLE IF NOT EXISTS app_schema.messages_channels(
    id SERIAL PRIMARY KEY,
    c_message_text VARCHAR NOT NULL,
    c_sender_id BIGINT REFERENCES app_schema.users(id) ON DELETE SET NULL,
    c_channel_id BIGINT REFERENCES app_schema.channels(id) ON DELETE SET NULL,
    c_timestamp TIMESTAMP NOT NULL
);