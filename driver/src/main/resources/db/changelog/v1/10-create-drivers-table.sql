CREATE TABLE IF NOT EXISTS drivers(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(64)        NOT NULL,
    email       VARCHAR(64)        NOT NULL,
    phone       VARCHAR(64)        NOT NULL,
    gender      VARCHAR(64)        NOT NULL,
    car_id      BIGINT REFERENCES cars (id),
    rating      NUMERIC(2, 1)      NOT NULL,
    created_at  TIMESTAMP(6) WITH TIME ZONE,
    modified_at TIMESTAMP(6) WITH TIME ZONE,
    is_deleted  boolean            NOT NULL
);