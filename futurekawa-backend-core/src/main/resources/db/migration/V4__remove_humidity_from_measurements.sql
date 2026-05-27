-- Remove humidity and sensor_id columns - temperature-only measurements
ALTER TABLE measurements
DROP COLUMN humidity;

ALTER TABLE measurements
DROP COLUMN sensor_id;
