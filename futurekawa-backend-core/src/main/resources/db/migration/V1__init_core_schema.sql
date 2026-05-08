-- =====================================================
-- FutureKawa Backend Core - Initial Schema
-- =====================================================

CREATE TABLE warehouses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    ideal_temperature NUMERIC(5,2) NOT NULL,
    ideal_humidity NUMERIC(5,2) NOT NULL,
    tolerance_temperature NUMERIC(5,2) NOT NULL DEFAULT 3.0,
    tolerance_humidity NUMERIC(5,2) NOT NULL DEFAULT 2.0
);

CREATE TABLE stocks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    warehouse_id UUID NOT NULL REFERENCES warehouses(id) ON DELETE CASCADE,
    reference VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    quality_score NUMERIC(5,2)
);

CREATE INDEX idx_stock_warehouse ON stocks(warehouse_id);
CREATE INDEX idx_stock_created_at ON stocks(created_at);
CREATE INDEX idx_stock_status ON stocks(status);

CREATE TABLE measurements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    stock_id UUID NOT NULL REFERENCES stocks(id) ON DELETE CASCADE,
    measured_at TIMESTAMP NOT NULL,
    temperature NUMERIC(5,2) NOT NULL,
    humidity NUMERIC(5,2) NOT NULL,
    sensor_id VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_measurement_stock ON measurements(stock_id);
CREATE INDEX idx_measurement_measured_at ON measurements(measured_at);

CREATE TABLE alerts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    stock_id UUID NOT NULL REFERENCES stocks(id) ON DELETE CASCADE,
    alerted_at TIMESTAMP NOT NULL,
    type VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    email_sent BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_alert_stock ON alerts(stock_id);
CREATE INDEX idx_alert_alerted_at ON alerts(alerted_at);
CREATE INDEX idx_alert_sent ON alerts(email_sent);
