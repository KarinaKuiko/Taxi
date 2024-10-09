CREATE TABLE IF NOT EXISTS rides (
    id            BIGSERIAL      PRIMARY KEY,
    driver_id     BIGINT,
    passenger_id  BIGINT         NOT NULL,
    address_from  VARCHAR(64)    NOT NULL,
    address_to    VARCHAR(64)    NOT NULL,
    ride_status   VARCHAR(64)    NOT NULL,
    cost          NUMERIC(6, 2)  NOT NULL,
    created_at    TIMESTAMP(6) WITH TIME ZONE,
    modified_at   TIMESTAMP(6) WITH TIME ZONE
);