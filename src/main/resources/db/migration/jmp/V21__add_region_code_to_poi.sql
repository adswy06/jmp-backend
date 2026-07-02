-- Migration: V21__add_region_code_to_poi.sql
-- Description: Add region_code column to m_poi table.

ALTER TABLE m_poi ADD COLUMN IF NOT EXISTS region_code VARCHAR(50);

CREATE INDEX IF NOT EXISTS idx_m_poi_region_code
ON m_poi(region_code);
