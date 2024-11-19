TRUNCATE TABLE cars CASCADE;
ALTER SEQUENCE cars_id_seq RESTART WITH 1;
INSERT INTO cars (color, brand, number, year, is_deleted)
VALUES ('red', 'BMW', 'AB123CD', 2023, FALSE),
       ('red', 'BMW', 'LK124AS', 2023, FALSE);