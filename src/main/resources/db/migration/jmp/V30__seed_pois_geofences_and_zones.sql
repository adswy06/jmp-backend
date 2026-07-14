-- 1. Insert official Zone for Cakung
INSERT INTO m_zone (id, name, address, paths, created_by)
VALUES ('zone-cakung-industri', 'Cakung Industrial Zone', 'Kawasan Industri Cakung', 
'{"points": [{"lat": -6.175000, "lng": 106.945000}, {"lat": -6.175000, "lng": 106.955000}, {"lat": -6.185000, "lng": 106.955000}, {"lat": -6.185000, "lng": 106.945000}]}'::jsonb, 
'SYSTEM')
ON CONFLICT (id) DO NOTHING;

-- 2. Update real coordinates and link zone in m_poi table
UPDATE m_poi SET lat = -6.180000, lng = 106.950000, zone_id = 'zone-cakung-industri' WHERE id = 'p0f865f8-8bb8-4ecf-9177-d64e9e03d311'; -- Pabrik Jakarta Barat (Cakung)
UPDATE m_poi SET lat = -6.101230, lng = 106.884560, zone_id = NULL WHERE id = 'p0f865f8-8bb8-4ecf-9177-d64e9e03d312'; -- Pelabuhan Tanjung Priok JKT
UPDATE m_poi SET lat = -7.250000, lng = 112.720000, zone_id = NULL WHERE id = 'p0f865f8-8bb8-4ecf-9177-d64e9e03d313'; -- Gudang Surabaya Margomulyo

-- 3. Clear existing geofences for these POIs to prevent constraint violations
DELETE FROM geofence WHERE poi_id IN ('p0f865f8-8bb8-4ecf-9177-d64e9e03d311', 'p0f865f8-8bb8-4ecf-9177-d64e9e03d312', 'p0f865f8-8bb8-4ecf-9177-d64e9e03d313');

-- 4. Seed RADIUS geofence for Pelabuhan Tanjung Priok JKT
INSERT INTO geofence (id, poi_id, route_id, shapeType, radius, paths)
VALUES ('geo-priok-radius', 'p0f865f8-8bb8-4ecf-9177-d64e9e03d312', NULL, 'RADIUS', 500, 
'{"center": {"lat": -6.101230, "lng": 106.884560}}'::jsonb);

-- 5. Seed POLYGON geofence for Pelabuhan Tanjung Priok JKT
INSERT INTO geofence (id, poi_id, route_id, shapeType, radius, paths)
VALUES ('geo-priok-polygon', 'p0f865f8-8bb8-4ecf-9177-d64e9e03d312', NULL, 'POLYGON', NULL, 
'{"points": [{"lat": -6.099000, "lng": 106.882000}, {"lat": -6.099000, "lng": 106.887000}, {"lat": -6.103000, "lng": 106.887000}, {"lat": -6.103000, "lng": 106.882000}]}'::jsonb);

-- 6. Seed ZONE geofence for Pabrik Jakarta Barat (Cakung)
INSERT INTO geofence (id, poi_id, route_id, shapeType, radius, paths)
VALUES ('geo-cakung-zone', 'p0f865f8-8bb8-4ecf-9177-d64e9e03d311', NULL, 'ZONE', NULL, 
'{"zoneId": "zone-cakung-industri", "points": [{"lat": -6.175000, "lng": 106.945000}, {"lat": -6.175000, "lng": 106.955000}, {"lat": -6.185000, "lng": 106.955000}, {"lat": -6.185000, "lng": 106.945000}]}'::jsonb);
