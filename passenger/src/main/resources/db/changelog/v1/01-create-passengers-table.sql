CREATE TABLE IF NOT EXISTS passengers(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(64)        NOT NULL,
    email       VARCHAR(64)        NOT NULL,
    phone       VARCHAR(64)        NOT NULL,
    rating      NUMERIC(2,1)       NOT NULL,
    created_at  TIMESTAMP(6) WITH TIME ZONE,
    modified_at TIMESTAMP(6) WITH TIME ZONE,
    is_deleted  boolean            NOT NULL
);