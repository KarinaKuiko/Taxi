ALTER TABLE drivers
ADD COLUMN name VARCHAR(255);

UPDATE drivers
SET name = first_name || ' ' || last_name;

ALTER TABLE drivers
DROP COLUMN first_name,
DROP COLUMN last_name;