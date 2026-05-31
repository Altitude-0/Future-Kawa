-- =====================================================
-- FutureKawa - Seed Data Script
-- Populates the new schema with test data (English version)
-- =====================================================

-- 0. CLEAN EXISTING DATA
TRUNCATE TABLE country CASCADE;
TRUNCATE TABLE warehouse CASCADE;
TRUNCATE TABLE sensors_type CASCADE;

-- 2. SENSORS TYPE
DELETE FROM sensors_type WHERE sensor_type = 'DHT11';
INSERT INTO sensors_type (id, sensor_type) VALUES 
('f1111111-89ab-cdef-0123-456789abcdef', 'DHT11');

-- 3. COUNTRY (Unique ISO Code)
INSERT INTO country (id, code_iso, name) VALUES
('c1111111-89ab-cdef-0123-456789abcdef', 'BR', 'Brazil'),
('c2222222-89ab-cdef-0123-456789abcdef', 'CO', 'Colombia'),
('c3333333-89ab-cdef-0123-456789abcdef', 'EC', 'Ecuador');

-- 4. CONFIGURATIONS (Quality standards per country)
INSERT INTO configurations (id, fk_country, temperature_ideal, temperature_tolerance, humidity_ideal, humidity_tolerance, temperature_unit, created_at) VALUES
(gen_random_uuid(), 'c1111111-89ab-cdef-0123-456789abcdef', 29.0, 3.0, 55.0, 2.0, 'CELSIUS', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'c2222222-89ab-cdef-0123-456789abcdef', 26.0, 3.0, 80.0, 2.0, 'CELSIUS', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'c3333333-89ab-cdef-0123-456789abcdef', 31.0, 3.0, 60.0, 2.0, 'CELSIUS', CURRENT_TIMESTAMP);

-- 5. WAREHOUSE
-- Added ideal_temperature (mandatory from V1/V5)
INSERT INTO warehouse (id, name, fk_country, ideal_temperature, tolerance_temperature) VALUES
('11111111-1111-1111-1111-111111111111', 'Sao Paulo Warehouse', 'c1111111-89ab-cdef-0123-456789abcdef', 29.0, 3.0),
('22222222-2222-2222-2222-222222222222', 'Medellin Warehouse', 'c2222222-89ab-cdef-0123-456789abcdef', 26.0, 3.0);

-- 6. SENSORS (Linked to DHT11 type)
INSERT INTO sensors (id, fk_sensor_type, entry_date, reference) VALUES
('01111111-1111-1111-1111-111111111111', 'f1111111-89ab-cdef-0123-456789abcdef', CURRENT_TIMESTAMP, 'BR-SP-SENS-001'),
('02222222-2222-2222-2222-222222222222', 'f1111111-89ab-cdef-0123-456789abcdef', CURRENT_TIMESTAMP, 'CO-MED-SENS-002');

-- 7. CONTAINERS
INSERT INTO containers (id, fk_warehouse, entry_date, id_sensor, status, reference) VALUES
('bc111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', CURRENT_TIMESTAMP - INTERVAL '10 days', '01111111-1111-1111-1111-111111111111', 'COMPLIANT', 'CONT-001'),
('bc222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', CURRENT_TIMESTAMP - INTERVAL '400 days', '02222222-2222-2222-2222-222222222222', 'OUTDATED', 'CONT-002');

-- 8. MEASUREMENTS
INSERT INTO measurements (id, fk_sensors, temperature, humidity, created_at) VALUES
(gen_random_uuid(), '01111111-1111-1111-1111-111111111111', 28.5, 54.5, CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), '01111111-1111-1111-1111-111111111111', 33.0, 58.0, CURRENT_TIMESTAMP);

-- 9. ALERTS
INSERT INTO alerts (id, fk_containers, type, alerted_at, email_sent, description, created_at) VALUES
(gen_random_uuid(), 'bc111111-1111-1111-1111-111111111111', 'TEMPERATURE_OUT_OF_RANGE', CURRENT_TIMESTAMP, false, 'Temperature too high', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'bc222222-2222-2222-2222-222222222222', 'OUTDATED_CONTAINER', CURRENT_TIMESTAMP - INTERVAL '5 days', true, 'Container expired', CURRENT_TIMESTAMP - INTERVAL '5 days');
