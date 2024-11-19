TRUNCATE TABLE passengers;
ALTER SEQUENCE passengers_id_seq RESTART WITH 1;
INSERT INTO passengers (name, email, phone, rating, is_deleted)
VALUES ('passenger', 'passenger@gmail.com', '+375441234567', 5.0, FALSE);