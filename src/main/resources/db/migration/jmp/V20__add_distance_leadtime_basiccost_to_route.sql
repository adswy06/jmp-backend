-- Migration: V20__add_distance_leadtime_basiccost_to_route.sql
-- Description: Add distance_km, journey_leadtime, and basic_cost fields to m_route table.

ALTER TABLE m_route ADD COLUMN IF NOT EXISTS distance_km DOUBLE PRECISION;
ALTER TABLE m_route ADD COLUMN IF NOT EXISTS journey_leadtime INTEGER;
ALTER TABLE m_route ADD COLUMN IF NOT EXISTS basic_cost DOUBLE PRECISION;

-- Update mock route with sample values
UPDATE m_route 
SET distance_km = 782.5, journey_leadtime = 840, basic_cost = 2500000.00 
WHERE id = 'r0f865f8-8bb8-4ecf-9177-d64e9e03d301';
