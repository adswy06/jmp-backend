ALTER TABLE m_route_point
ALTER COLUMN paths
TYPE JSON
USING paths::json;

ALTER TABLE m_zone
ALTER COLUMN paths
TYPE JSON
USING paths::json;

ALTER TABLE geofence
ALTER COLUMN paths
TYPE JSON
USING paths::json;