TRUNCATE TABLE driver_rates;
ALTER SEQUENCE rating_id_seq RESTART WITH 1;
INSERT INTO driver_rates (ride_id, comment, rating, user_type, user_id)
VALUES (1, 'Good', 4, 'PASSENGER', 1);