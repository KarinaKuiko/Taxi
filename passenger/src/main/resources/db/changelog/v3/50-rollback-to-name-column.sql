ALTER TABLE passengers
ADD COLUMN name VARCHAR(255);

UPDATE passengers
SET name = first_name || ' ' || last_name;

ALTER TABLE passengers
DROP COLUMN first_name,
DROP COLUMN last_name;