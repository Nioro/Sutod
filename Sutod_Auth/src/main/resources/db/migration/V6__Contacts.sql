CREATE TABLE IF NOT EXISTS app_schema.contacts (
    user_id BIGINT REFERENCES app_schema.users(id) ON DELETE CASCADE,
    user_in_contact_id BIGINT REFERENCES app_schema.users(id) ON DELETE CASCADE,
    CONSTRAINT contact_pkey PRIMARY KEY (user_id, user_in_contact_id)
);