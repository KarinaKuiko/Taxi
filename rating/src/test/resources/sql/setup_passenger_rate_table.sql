TRUNCATE TABLE passenger_rates;
ALTER SEQUENCE passenger_rates_id_seq RESTART WITH 1;
INSERT INTO passenger_rates (ride_id, comment, rating, user_type, user_id)
VALUES (1, 'Good', 4, 'DRIVER', 1);