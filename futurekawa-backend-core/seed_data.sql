-- =====================================================
-- FutureKawa - Seed Data Script
-- Script de population de la base de données avec des données de test
-- =====================================================

-- =====================================================
-- 1. UTILISATEURS
-- =====================================================
INSERT INTO users (id, username, email, password, first_name, last_name, role, enabled, created_at) VALUES
('a1234567-89ab-cdef-0123-456789abcdef', 'admin_user', 'admin@futurekawa.local', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5NLhK/h3k7dNK', 'Admin', 'User', 'ADMIN', true, CURRENT_TIMESTAMP),
('b1234567-89ab-cdef-0123-456789abcdef', 'manager_user', 'manager@futurekawa.local', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5NLhK/h3k7dNK', 'Manager', 'User', 'MANAGER', true, CURRENT_TIMESTAMP),
('c1234567-89ab-cdef-0123-456789abcdef', 'viewer_user', 'viewer@futurekawa.local', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5NLhK/h3k7dNK', 'Viewer', 'User', 'VIEWER', true, CURRENT_TIMESTAMP);

-- =====================================================
-- 2. ENTREPÔTS (Warehouses)
-- =====================================================
INSERT INTO warehouses (id, name, ideal_temperature, ideal_humidity, tolerance_temperature, tolerance_humidity) VALUES
('d1234567-89ab-cdef-0123-456789abcdef', 'Entrepôt São Paulo', 22.0, 60.0, 3.0, 2.0),
('e1234567-89ab-cdef-0123-456789abcdef', 'Entrepôt Medellin', 20.0, 55.0, 3.0, 2.0),
('f1234567-89ab-cdef-0123-456789abcdef', 'Entrepôt Quito', 18.0, 65.0, 3.0, 2.0);

-- =====================================================
-- 3. STOCKS (Lots de café)
-- =====================================================
INSERT INTO stocks (id, warehouse_id, reference, status, quality_score, created_at) VALUES
-- Stocks São Paulo
('11111111-89ab-cdef-0123-456789abcdef', 'd1234567-89ab-cdef-0123-456789abcdef', 'SP-2024-001-ARABICA', 'COMPLIANT', 8.5, CURRENT_TIMESTAMP - INTERVAL '45 days'),
('12222222-89ab-cdef-0123-456789abcdef', 'd1234567-89ab-cdef-0123-456789abcdef', 'SP-2024-002-ROBUSTA', 'COMPLIANT', 7.8, CURRENT_TIMESTAMP - INTERVAL '30 days'),
('13333333-89ab-cdef-0123-456789abcdef', 'd1234567-89ab-cdef-0123-456789abcdef', 'SP-2024-003-ARABICA-PREMIUM', 'ALERT', 9.2, CURRENT_TIMESTAMP - INTERVAL '15 days'),

-- Stocks Medellin
('21111111-89ab-cdef-0123-456789abcdef', 'e1234567-89ab-cdef-0123-456789abcdef', 'MD-2024-001-GEISHA', 'COMPLIANT', 9.5, CURRENT_TIMESTAMP - INTERVAL '50 days'),
('22222222-89ab-cdef-0123-456789abcdef', 'e1234567-89ab-cdef-0123-456789abcdef', 'MD-2024-002-STANDARD', 'COMPLIANT', 7.2, CURRENT_TIMESTAMP - INTERVAL '20 days'),

-- Stocks Quito
('31111111-89ab-cdef-0123-456789abcdef', 'f1234567-89ab-cdef-0123-456789abcdef', 'EQ-2024-001-ORGANICO', 'COMPLIANT', 8.8, CURRENT_TIMESTAMP - INTERVAL '25 days'),
('32222222-89ab-cdef-0123-456789abcdef', 'f1234567-89ab-cdef-0123-456789abcdef', 'EQ-2024-002-SPECIAL', 'EXPIRED', 8.1, CURRENT_TIMESTAMP - INTERVAL '60 days');

-- =====================================================
-- 4. MESURES DE TEMPÉRATURE ET HUMIDITÉ
-- =====================================================
-- Mesures pour SP-2024-001
INSERT INTO measurements (id, stock_id, measured_at, temperature, humidity, sensor_id, created_at) VALUES
('41111111-89ab-cdef-0123-456789abcdef', '11111111-89ab-cdef-0123-456789abcdef', CURRENT_TIMESTAMP - INTERVAL '48 hours', 22.3, 61.2, 'SENSOR-SP-01', CURRENT_TIMESTAMP - INTERVAL '48 hours'),
('41111112-89ab-cdef-0123-456789abcdef', '11111111-89ab-cdef-0123-456789abcdef', CURRENT_TIMESTAMP - INTERVAL '36 hours', 21.8, 59.5, 'SENSOR-SP-01', CURRENT_TIMESTAMP - INTERVAL '36 hours'),
('41111113-89ab-cdef-0123-456789abcdef', '11111111-89ab-cdef-0123-456789abcdef', CURRENT_TIMESTAMP - INTERVAL '24 hours', 22.1, 60.8, 'SENSOR-SP-01', CURRENT_TIMESTAMP - INTERVAL '24 hours'),
('41111114-89ab-cdef-0123-456789abcdef', '11111111-89ab-cdef-0123-456789abcdef', CURRENT_TIMESTAMP - INTERVAL '12 hours', 22.5, 62.1, 'SENSOR-SP-01', CURRENT_TIMESTAMP - INTERVAL '12 hours'),

-- Mesures pour SP-2024-003 (Stock en ALERTE - température trop élevée)
('41333331-89ab-cdef-0123-456789abcdef', '13333333-89ab-cdef-0123-456789abcdef', CURRENT_TIMESTAMP - INTERVAL '6 hours', 25.8, 65.2, 'SENSOR-SP-03', CURRENT_TIMESTAMP - INTERVAL '6 hours'),
('41333332-89ab-cdef-0123-456789abcdef', '13333333-89ab-cdef-0123-456789abcdef', CURRENT_TIMESTAMP - INTERVAL '3 hours', 26.2, 67.1, 'SENSOR-SP-03', CURRENT_TIMESTAMP - INTERVAL '3 hours'),
('41333333-89ab-cdef-0123-456789abcdef', '13333333-89ab-cdef-0123-456789abcdef', CURRENT_TIMESTAMP - INTERVAL '1 hours', 26.5, 68.3, 'SENSOR-SP-03', CURRENT_TIMESTAMP - INTERVAL '1 hours'),

-- Mesures pour MD-2024-001
('42111111-89ab-cdef-0123-456789abcdef', '21111111-89ab-cdef-0123-456789abcdef', CURRENT_TIMESTAMP - INTERVAL '2 hours', 20.3, 54.8, 'SENSOR-MD-01', CURRENT_TIMESTAMP - INTERVAL '2 hours'),
('42111112-89ab-cdef-0123-456789abcdef', '21111111-89ab-cdef-0123-456789abcdef', CURRENT_TIMESTAMP - INTERVAL '1 hours', 20.1, 55.2, 'SENSOR-MD-01', CURRENT_TIMESTAMP - INTERVAL '1 hours'),

-- Mesures pour EQ-2024-002 (Stock en ALERTE - très ancien)
('43222221-89ab-cdef-0123-456789abcdef', '32222222-89ab-cdef-0123-456789abcdef', CURRENT_TIMESTAMP - INTERVAL '4 hours', 18.5, 64.2, 'SENSOR-EQ-02', CURRENT_TIMESTAMP - INTERVAL '4 hours'),
('43222222-89ab-cdef-0123-456789abcdef', '32222222-89ab-cdef-0123-456789abcdef', CURRENT_TIMESTAMP - INTERVAL '2 hours', 18.7, 65.1, 'SENSOR-EQ-02', CURRENT_TIMESTAMP - INTERVAL '2 hours');

-- =====================================================
-- 5. ALERTES
-- =====================================================
INSERT INTO alerts (id, stock_id, type, description, alerted_at, email_sent, created_at) VALUES
-- Alerte température pour SP-2024-003
('51333331-89ab-cdef-0123-456789abcdef', '13333333-89ab-cdef-0123-456789abcdef', 'TEMPERATURE_OUT_OF_RANGE', 'Température élevée détectée: 26.5°C (seuil max: 23°C)', CURRENT_TIMESTAMP - INTERVAL '1 hours', false, CURRENT_TIMESTAMP - INTERVAL '1 hours'),

-- Alerte humidité pour SP-2024-003
('51333332-89ab-cdef-0123-456789abcdef', '13333333-89ab-cdef-0123-456789abcdef', 'HUMIDITY_OUT_OF_RANGE', 'Humidité élevée détectée: 68.3% (seuil max: 62%)', CURRENT_TIMESTAMP - INTERVAL '50 minutes', false, CURRENT_TIMESTAMP - INTERVAL '50 minutes'),

-- Alerte ancienneté pour EQ-2024-002
('53222221-89ab-cdef-0123-456789abcdef', '32222222-89ab-cdef-0123-456789abcdef', 'OLD_LOT_WARNING', 'Lot stocké depuis 60 jours, vérification de qualité recommandée', CURRENT_TIMESTAMP - INTERVAL '5 days', false, CURRENT_TIMESTAMP - INTERVAL '5 days');

-- =====================================================
-- Vérification des insertions
-- =====================================================
SELECT 'Utilisateurs' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'Entrepôts', COUNT(*) FROM warehouses
UNION ALL
SELECT 'Stocks', COUNT(*) FROM stocks
UNION ALL
SELECT 'Mesures', COUNT(*) FROM measurements
UNION ALL
SELECT 'Alertes', COUNT(*) FROM alerts;
