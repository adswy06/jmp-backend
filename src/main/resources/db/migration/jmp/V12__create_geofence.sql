CREATE TABLE IF NOT EXISTS geofence (
    id VARCHAR(40) PRIMARY KEY DEFAULT gen_random_uuid(),
    poi_id VARCHAR NOT NULL,
    route_id VARCHAR,
    shapeType VARCHAR(50) NOT NULL,
    radius INTEGER,
    paths JSONB,

    CONSTRAINT fk_geofence_poi
        FOREIGN KEY (poi_id)
        REFERENCES m_poi(id),

    CONSTRAINT fk_geofence_route
        FOREIGN KEY (route_id)
        REFERENCES m_route(id)
);

CREATE INDEX IF NOT EXISTS idx_geofence_poi
ON geofence(poi_id);

CREATE INDEX IF NOT EXISTS idx_geofence_route
ON geofence(route_id);

CREATE INDEX IF NOT EXISTS idx_geofence_shape
ON geofence(shapeType);