-- Remove humidity thresholds from all tables
-- Keep only temperature thresholds - humidity management deferred to future

-- Remove from warehouses
ALTER TABLE warehouses
DROP COLUMN ideal_humidity;

ALTER TABLE warehouses
DROP COLUMN tolerance_humidity;

-- Remove from configurations
ALTER TABLE configurations
DROP COLUMN humidity_ideal;

ALTER TABLE configurations
DROP COLUMN humidity_tolerance;
