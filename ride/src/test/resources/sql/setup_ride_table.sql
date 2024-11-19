TRUNCATE TABLE rides;
ALTER SEQUENCE rides_id_seq RESTART WITH 1;
INSERT INTO rides (driver_id, passenger_id, address_from, address_to, driver_ride_status, cost, passenger_ride_status)
VALUES (1, 1, 'from', 'to', 'ACCEPTED', 123.45, 'WAITING'),
       (1, 1, 'from', 'to', 'WAITING', 123.45, 'WAITING');