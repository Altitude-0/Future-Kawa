-- =====================================================
-- FutureKawa - Clean Data Script
-- Script to completely remove test data (Truncate Cascade)
-- =====================================================

TRUNCATE TABLE 
    configurations_audits,
    alerts,
    measurements,
    containers,
    sensors,
    warehouse,
    configurations,
    country,
    users,
    sensors_type 
CASCADE;

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
