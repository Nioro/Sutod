CREATE TABLE IF NOT EXISTS app_schema.channels_users (
    user_id BIGINT REFERENCES app_schema.users(id) ON DELETE CASCADE,
    channel_id BIGINT REFERENCES app_schema.channels(id) ON DELETE CASCADE,
    CONSTRAINT channel_user_pkey PRIMARY KEY (user_id, channel_id)
);