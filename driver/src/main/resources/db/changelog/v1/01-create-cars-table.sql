CREATE TABLE IF NOT EXISTS cars (
    id          BIGSERIAL     PRIMARY KEY,
    color       VARCHAR(64)   NOT NULL,
    brand       VARCHAR(64)   NOT NULL,
    number      VARCHAR(64)   NOT NULL,
    year        int           NOT NULL,
    created_at  TIMESTAMP(6) WITH TIME ZONE,
    modified_at TIMESTAMP(6) WITH TIME ZONE,
    is_deleted  boolean       NOT NULL
);