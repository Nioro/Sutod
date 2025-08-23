CREATE TABLE IF NOT EXISTS app_schema.groups_users (
    user_id BIGINT REFERENCES app_schema.users(id) ON DELETE CASCADE,
    group_id BIGINT REFERENCES app_schema.groups(id) ON DELETE CASCADE,
    CONSTRAINT group_user_pkey PRIMARY KEY (user_id, group_id)
);