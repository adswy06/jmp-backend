-- Migration: V22__add_cost_and_lead_time_to_activity.sql
-- Description: Add cost and lead_time columns to m_activity table and seed with default values.

-- 1. Add cost and lead_time columns to m_activity table
ALTER TABLE m_activity 
ADD COLUMN IF NOT EXISTS cost NUMERIC(15,2) DEFAULT 0.00,
ADD COLUMN IF NOT EXISTS lead_time INT DEFAULT 0;

-- 2. Update default cost and lead time values for seeded activities
UPDATE m_activity SET cost = 150000.00, lead_time = 120 WHERE name = 'Pemuatan Barang';
UPDATE m_activity SET cost = 120000.00, lead_time = 90 WHERE name = 'Pembongkaran Barang';
UPDATE m_activity SET cost = 500000.00, lead_time = 180 WHERE name = 'Kepabeanan';
UPDATE m_activity SET cost = 50000.00, lead_time = 15 WHERE name = 'Timbang Masuk';
UPDATE m_activity SET cost = 50000.00, lead_time = 15 WHERE name = 'Timbang Keluar';
UPDATE m_activity SET cost = 0.00, lead_time = 30 WHERE name = 'Administrasi Dokumen';
