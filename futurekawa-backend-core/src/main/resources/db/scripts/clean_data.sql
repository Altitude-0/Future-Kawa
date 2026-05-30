-- =====================================================
-- FutureKawa - Clean Data Script
-- Script to remove test data in correct order
-- =====================================================

-- 1. Remove Audits (depend on configurations)
DELETE FROM configurations_audits;

-- 2. Remove Alerts (depend on containers)
DELETE FROM alerts;

-- 3. Remove Measurements (depend on sensors)
DELETE FROM measurements;

-- 4. Remove Containers (depend on warehouses and sensors)
DELETE FROM containers;

-- 5. Remove Sensors (depend on sensors_type)
DELETE FROM sensors;

-- 6. Remove Warehouses (depend on country)
DELETE FROM warehouse;

-- 7. Remove Configurations (depend on country)
DELETE FROM configurations;

-- 8. Remove Country (dependencies now empty)
DELETE FROM country;

-- 9. Remove Users
DELETE FROM users;

-- 10. Remove Sensors Type (last)
DELETE FROM sensors_type;

-- Verification
SELECT 'Utilisateurs restants' as description, COUNT(*) as count FROM users
UNION ALL
SELECT 'Entrepôts restants', COUNT(*) FROM warehouse
UNION ALL
SELECT 'Containers restants', COUNT(*) FROM containers
UNION ALL
SELECT 'Capteurs restants', COUNT(*) FROM sensors
UNION ALL
SELECT 'Mesures restantes', COUNT(*) FROM measurements
UNION ALL
SELECT 'Alertes restantes', COUNT(*) FROM alerts
UNION ALL
SELECT 'Pays restants', COUNT(*) FROM country;
