-- =====================================================
-- FutureKawa - Country and Configuration Tables
-- =====================================================

-- =====================================================
-- 1. CREATE TABLE countries
-- =====================================================
CREATE TABLE countries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(2) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 2. ALTER TABLE warehouses - Add country_id FK
-- =====================================================
ALTER TABLE warehouses
ADD COLUMN country_id UUID REFERENCES countries(id) ON DELETE RESTRICT;

-- =====================================================
-- 3. CREATE TABLE configurations
-- =====================================================
CREATE TABLE configurations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    country_id UUID NOT NULL UNIQUE REFERENCES countries(id) ON DELETE CASCADE,
    temperature_ideal NUMERIC(5,2) NOT NULL,
    humidity_ideal NUMERIC(5,2) NOT NULL,
    temperature_tolerance NUMERIC(5,2) NOT NULL,
    humidity_tolerance NUMERIC(5,2) NOT NULL,
    temperature_unit VARCHAR(20) NOT NULL DEFAULT 'CELSIUS',
    alert_old_lot_days INTEGER NOT NULL DEFAULT 365,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- =====================================================
-- 4. CREATE TABLE configuration_audits
-- =====================================================
CREATE TABLE configuration_audits (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    configuration_id UUID NOT NULL REFERENCES configurations(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE SET NULL,
    field_name VARCHAR(100) NOT NULL,
    old_value VARCHAR(255),
    new_value VARCHAR(255),
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 5. CREATE INDEXES
-- =====================================================
CREATE INDEX idx_config_country_id ON configurations(country_id);
CREATE INDEX idx_audit_config_id ON configuration_audits(configuration_id);
CREATE INDEX idx_audit_user_id ON configuration_audits(user_id);
CREATE INDEX idx_audit_changed_at ON configuration_audits(changed_at);

-- =====================================================
-- 6. INSERT SEED DATA - Countries
-- =====================================================
INSERT INTO countries (code, name) VALUES
('BR', 'Brésil'),
('EC', 'Équateur'),
('CO', 'Colombie');

-- =====================================================
-- 7. UPDATE warehouses - Assign countries to existing warehouses
-- =====================================================
UPDATE warehouses
SET country_id = (SELECT id FROM countries WHERE code = 'BR')
WHERE name = 'Entrepôt São Paulo';

UPDATE warehouses
SET country_id = (SELECT id FROM countries WHERE code = 'CO')
WHERE name = 'Entrepôt Medellin';

UPDATE warehouses
SET country_id = (SELECT id FROM countries WHERE code = 'EC')
WHERE name = 'Entrepôt Quito';

-- =====================================================
-- 8. INSERT SEED DATA - Configurations
-- =====================================================
INSERT INTO configurations (country_id, temperature_ideal, humidity_ideal, temperature_tolerance, humidity_tolerance, temperature_unit, alert_old_lot_days)
SELECT id, 22.0, 60.0, 3.0, 2.0, 'CELSIUS', 365 FROM countries WHERE code = 'BR'
UNION ALL
SELECT id, 18.0, 65.0, 3.0, 2.0, 'CELSIUS', 365 FROM countries WHERE code = 'EC'
UNION ALL
SELECT id, 20.0, 55.0, 3.0, 2.0, 'CELSIUS', 365 FROM countries WHERE code = 'CO';

-- =====================================================
-- 9. ALTER TABLE warehouses - Make country_id NOT NULL
-- =====================================================
ALTER TABLE warehouses
ALTER COLUMN country_id SET NOT NULL;

-- =====================================================
-- 10. VERIFICATION
-- =====================================================
SELECT 'Countries created' as status, COUNT(*) as count FROM countries
UNION ALL
SELECT 'Warehouses with countries', COUNT(*) FROM warehouses WHERE country_id IS NOT NULL
UNION ALL
SELECT 'Configurations created', COUNT(*) FROM configurations;
