CREATE TABLE IF NOT EXISTS rating (
    id            BIGSERIAL      PRIMARY KEY,
    ride_id       BIGINT         NOT NULL,
    comment       VARCHAR(64),
    rating        INT            NOT NULL,
    user_type     VARCHAR(64),
    user_id       BIGINT,
    created_at    TIMESTAMP(6) WITH TIME ZONE,
    modified_at   TIMESTAMP(6) WITH TIME ZONE
);