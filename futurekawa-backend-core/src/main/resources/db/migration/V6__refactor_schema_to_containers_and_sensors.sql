-- =====================================================
-- V6__refactor_schema_to_containers_and_sensors.sql
-- Refactor schema to match Modification.md EXACTLY
-- =====================================================

-- 1. Table COUNTRY (Singular)
ALTER TABLE IF EXISTS countries RENAME TO country;
ALTER TABLE country RENAME COLUMN code TO code_iso;

-- 2. Table WAREHOUSE (Singular)
ALTER TABLE IF EXISTS warehouses RENAME TO warehouse;
ALTER TABLE warehouse RENAME COLUMN country_id TO fk_country;
ALTER TABLE warehouse DROP CONSTRAINT IF EXISTS warehouses_country_id_fkey;
ALTER TABLE warehouse ADD CONSTRAINT fk_warehouse_country FOREIGN KEY (fk_country) REFERENCES country(id);

-- 3. SENSORS_TYPE
CREATE TABLE IF NOT EXISTS sensors_type (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sensor_type VARCHAR(100) NOT NULL
);

-- 4. SENSORS
CREATE TABLE IF NOT EXISTS sensors (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    fk_sensor_type UUID NOT NULL,
    entry_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reference VARCHAR(255) NOT NULL,
    CONSTRAINT fk_sensor_type FOREIGN KEY (fk_sensor_type) REFERENCES sensors_type(id)
);

-- 5. CONTAINERS
ALTER TABLE IF EXISTS stocks RENAME TO containers;
ALTER TABLE containers RENAME COLUMN warehouse_id TO fk_warehouse;
ALTER TABLE containers RENAME COLUMN created_at TO entry_date;
ALTER TABLE containers ADD COLUMN IF NOT EXISTS exit_date TIMESTAMP;
ALTER TABLE containers ADD COLUMN IF NOT EXISTS id_sensor UUID;
ALTER TABLE containers DROP CONSTRAINT IF EXISTS stocks_warehouse_id_fkey;
ALTER TABLE containers ADD CONSTRAINT fk_container_warehouse FOREIGN KEY (fk_warehouse) REFERENCES warehouse(id);
ALTER TABLE containers ADD CONSTRAINT fk_container_sensor FOREIGN KEY (id_sensor) REFERENCES sensors(id);

-- 6. MEASUREMENTS
ALTER TABLE measurements RENAME COLUMN stock_id TO fk_sensors;
-- On gère le conflit created_at : on garde l'existant ou on recrée
DO $$ 
BEGIN 
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='measurements' AND column_name='measured_at') THEN
        ALTER TABLE measurements DROP COLUMN IF EXISTS created_at;
        ALTER TABLE measurements RENAME COLUMN measured_at TO created_at;
    END IF;
END $$;
ALTER TABLE measurements ADD COLUMN IF NOT EXISTS humidity NUMERIC(5,2);
ALTER TABLE measurements DROP CONSTRAINT IF EXISTS measurements_stock_id_fkey;
ALTER TABLE measurements ADD CONSTRAINT fk_measurement_sensor FOREIGN KEY (fk_sensors) REFERENCES sensors(id);

-- 7. ALERTS
ALTER TABLE alerts RENAME COLUMN stock_id TO fk_containers;
ALTER TABLE alerts DROP CONSTRAINT IF EXISTS alerts_stock_id_fkey;
ALTER TABLE alerts ADD CONSTRAINT fk_alert_container FOREIGN KEY (fk_containers) REFERENCES containers(id);

-- 8. CONFIGURATIONS
ALTER TABLE configurations RENAME COLUMN country_id TO fk_country;
ALTER TABLE configurations ADD COLUMN IF NOT EXISTS humidity_ideal NUMERIC(5,2);
ALTER TABLE configurations ADD COLUMN IF NOT EXISTS humidity_tolerance NUMERIC(5,2);
ALTER TABLE configurations DROP CONSTRAINT IF EXISTS configurations_country_id_fkey;
ALTER TABLE configurations ADD CONSTRAINT fk_configuration_country FOREIGN KEY (fk_country) REFERENCES country(id);

-- 9. CONFIGURATIONS_AUDITS
ALTER TABLE IF EXISTS configuration_audits RENAME TO configurations_audits;
ALTER TABLE configurations_audits RENAME COLUMN configuration_id TO fk_configuration;
ALTER TABLE configurations_audits RENAME COLUMN user_id TO fk_user;
ALTER TABLE configurations_audits DROP CONSTRAINT IF EXISTS configuration_audits_configuration_id_fkey;
ALTER TABLE configurations_audits DROP CONSTRAINT IF EXISTS configuration_audits_user_id_fkey;
ALTER TABLE configurations_audits ADD CONSTRAINT fk_audit_configuration FOREIGN KEY (fk_configuration) REFERENCES configurations(id);
ALTER TABLE configurations_audits ADD CONSTRAINT fk_audit_user FOREIGN KEY (fk_user) REFERENCES users(id);

-- 10. SEED DATA
DELETE FROM sensors_type WHERE sensor_type = 'DHT11';
INSERT INTO sensors_type (sensor_type) VALUES ('DHT11');
