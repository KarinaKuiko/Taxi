TRUNCATE TABLE drivers;
ALTER SEQUENCE drivers_id_seq RESTART WITH 1;
INSERT INTO drivers (first_name, last_name, email, phone, rating, is_deleted, car_id, gender)
VALUES ('test', 'test', 'test@gmail.com', '+375297654321', 5.0, FALSE, 1, 'MALE'),
       ('test', 'test', 'name@gmail.com', '+375297654321', 5.0, FALSE, 1, 'MALE');