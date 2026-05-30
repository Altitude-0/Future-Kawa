-- =====================================================
-- FutureKawa - Seed Data Script
-- Populates the new schema with test data (English version)
-- =====================================================

-- 1. USERS
INSERT INTO users (id, username, email, password, role, enabled, created_at) VALUES
('a1234567-89ab-cdef-0123-456789abcdef', 'admin_user', 'admin@futurekawa.local', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5NLhK/h3k7dNK', 'ADMIN', true, CURRENT_TIMESTAMP),
('b1234567-89ab-cdef-0123-456789abcdef', 'manager_user', 'manager@futurekawa.local', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5NLhK/h3k7dNK', 'MANAGER', true, CURRENT_TIMESTAMP),
('c1234567-89ab-cdef-0123-456789abcdef', 'viewer_user', 'viewer@futurekawa.local', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5NLhK/h3k7dNK', 'VIEWER', true, CURRENT_TIMESTAMP);

-- 2. COUNTRY (Unique ISO Code)
INSERT INTO country (id, code_iso) VALUES
('c1111111-89ab-cdef-0123-456789abcdef', 'BR'),
('c2222222-89ab-cdef-0123-456789abcdef', 'CO'),
('c3333333-89ab-cdef-0123-456789abcdef', 'EC');

-- 3. CONFIGURATIONS (Quality standards per country)
INSERT INTO configurations (id, fk_country, temperature_ideal, temperature_tolerance, humidity_ideal, humidity_tolerance, temperature_unit, created_at) VALUES
(gen_random_uuid(), 'c1111111-89ab-cdef-0123-456789abcdef', 29.0, 3.0, 55.0, 2.0, 'CELSIUS', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'c2222222-89ab-cdef-0123-456789abcdef', 26.0, 3.0, 80.0, 2.0, 'CELSIUS', CURRENT_TIMESTAMP),
(gen_random_uuid(), 'c3333333-89ab-cdef-0123-456789abcdef', 31.0, 3.0, 60.0, 2.0, 'CELSIUS', CURRENT_TIMESTAMP);

-- 4. WAREHOUSE
INSERT INTO warehouse (id, name, fk_country) VALUES
('w1111111-89ab-cdef-0123-456789abcdef', 'Sao Paulo Warehouse', 'c1111111-89ab-cdef-0123-456789abcdef'),
('w2222222-89ab-cdef-0123-456789abcdef', 'Medellin Warehouse', 'c2222222-89ab-cdef-0123-456789abcdef');

-- 5. SENSORS (Linked to DHT11 type)
INSERT INTO sensors (id, fk_sensor_type, entry_date, reference) VALUES
('s1111111-89ab-cdef-0123-456789abcdef', (SELECT id FROM sensors_type WHERE sensor_type = 'DHT11' LIMIT 1), CURRENT_TIMESTAMP, 'BR / Sao Paulo / CONT-001'),
('s2222222-89ab-cdef-0123-456789abcdef', (SELECT id FROM sensors_type WHERE sensor_type = 'DHT11' LIMIT 1), CURRENT_TIMESTAMP, 'CO / Medellin / CONT-002');

-- 6. CONTAINERS
INSERT INTO containers (id, fk_warehouse, entry_date, id_sensor, status) VALUES
('bc111111-89ab-cdef-0123-456789abcdef', 'w1111111-89ab-cdef-0123-456789abcdef', CURRENT_TIMESTAMP - INTERVAL '10 days', 's1111111-89ab-cdef-0123-456789abcdef', 'compliant'),
('bc222222-89ab-cdef-0123-456789abcdef', 'w2222222-89ab-cdef-0123-456789abcdef', CURRENT_TIMESTAMP - INTERVAL '400 days', 's2222222-89ab-cdef-0123-456789abcdef', 'outdated');

-- 7. MEASUREMENTS
INSERT INTO measurements (id, fk_sensors, temperature, humidity, created_at) VALUES
(gen_random_uuid(), 's1111111-89ab-cdef-0123-456789abcdef', 28.5, 54.5, CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(gen_random_uuid(), 's1111111-89ab-cdef-0123-456789abcdef', 33.0, 58.0, CURRENT_TIMESTAMP);

-- 8. ALERTS
INSERT INTO alerts (id, fk_containers, type, alerted_at, email_sent, created_at) VALUES
(gen_random_uuid(), 'bc111111-89ab-cdef-0123-456789abcdef', 'TEMPERATURE_OUT_OF_RANGE', CURRENT_TIMESTAMP, false, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'bc222222-89ab-cdef-0123-456789abcdef', 'OUTDATED_CONTAINER', CURRENT_TIMESTAMP - INTERVAL '5 days', true, CURRENT_TIMESTAMP - INTERVAL '5 days');
