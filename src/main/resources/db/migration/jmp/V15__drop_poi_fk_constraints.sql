-- 1. Drop foreign key constraint on m_route_point
ALTER TABLE m_route_point DROP CONSTRAINT IF EXISTS fk_route_point_m_poi;

-- 2. Drop foreign key constraint on geofence
ALTER TABLE geofence DROP CONSTRAINT IF EXISTS fk_geofence_poi;
