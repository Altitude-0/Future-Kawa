-- =====================================================
-- FutureKawa - Clean Data Script
-- Script pour supprimer les données de test
-- =====================================================
-- IMPORTANT: Cet ordre de suppression respecte les contraintes
-- de clés étrangères (supprimer d'abord les tables dépendantes)

-- =====================================================
-- 1. Supprimer les AUDITS de CONFIGURATION (dépendent des configurations)
-- =====================================================
DELETE FROM configuration_audits;

-- =====================================================
-- 2. Supprimer les CONFIGURATIONS (dépendent des countries)
-- =====================================================
DELETE FROM configurations;

-- =====================================================
-- 3. Supprimer les ALERTES (dépendent des stocks)
-- =====================================================
DELETE FROM alerts
WHERE stock_id IN (
    SELECT id FROM stocks
    WHERE warehouse_id IN (
        'd1234567-89ab-cdef-0123-456789abcdef',
        'e1234567-89ab-cdef-0123-456789abcdef',
        'f1234567-89ab-cdef-0123-456789abcdef'
    )
);

-- =====================================================
-- 4. Supprimer les MESURES (dépendent des stocks)
-- =====================================================
DELETE FROM measurements
WHERE stock_id IN (
    SELECT id FROM stocks
    WHERE warehouse_id IN (
        'd1234567-89ab-cdef-0123-456789abcdef',
        'e1234567-89ab-cdef-0123-456789abcdef',
        'f1234567-89ab-cdef-0123-456789abcdef'
    )
);

-- =====================================================
-- 5. Supprimer les STOCKS (dépendent des warehouses)
-- =====================================================
DELETE FROM stocks
WHERE warehouse_id IN (
    'd1234567-89ab-cdef-0123-456789abcdef',
    'e1234567-89ab-cdef-0123-456789abcdef',
    'f1234567-89ab-cdef-0123-456789abcdef'
);

-- =====================================================
-- 6. Supprimer les ENTREPÔTS
-- =====================================================
DELETE FROM warehouses
WHERE id IN (
    'd1234567-89ab-cdef-0123-456789abcdef',
    'e1234567-89ab-cdef-0123-456789abcdef',
    'f1234567-89ab-cdef-0123-456789abcdef'
);

-- =====================================================
-- 7. Supprimer les UTILISATEURS
-- =====================================================
DELETE FROM users
WHERE id IN (
    'a1234567-89ab-cdef-0123-456789abcdef',
    'b1234567-89ab-cdef-0123-456789abcdef',
    'c1234567-89ab-cdef-0123-456789abcdef'
);

-- =====================================================
-- 8. Supprimer les PAYS (dépendances maintenant vides)
-- =====================================================
DELETE FROM countries
WHERE code IN ('BR', 'EC', 'CO');

-- =====================================================
-- Vérification finale
-- =====================================================
SELECT 'Utilisateurs restants' as description, COUNT(*) as count FROM users
UNION ALL
SELECT 'Entrepôts restants', COUNT(*) FROM warehouses
UNION ALL
SELECT 'Stocks restants', COUNT(*) FROM stocks
UNION ALL
SELECT 'Mesures restantes', COUNT(*) FROM measurements
UNION ALL
SELECT 'Alertes restantes', COUNT(*) FROM alerts
UNION ALL
SELECT 'Pays restants', COUNT(*) FROM countries;
