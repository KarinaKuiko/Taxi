ALTER TABLE passengers
ADD COLUMN first_name VARCHAR(255),
ADD COLUMN last_name VARCHAR(255);

UPDATE passengers
SET first_name = SPLIT_PART(name, ' ', 1),
    last_name = SPLIT_PART(name, ' ', 2);

ALTER TABLE passengers
DROP COLUMN name;