CREATE TABLE m_road_hazard (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    hazard_type VARCHAR(50) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    geom GEOMETRY(Point, 4326),
    radius INT NOT NULL DEFAULT 50,
    severity VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_m_road_hazard_geom ON m_road_hazard USING GIST (geom);

-- Automatically sync PostGIS geometry from latitude & longitude
CREATE OR REPLACE FUNCTION update_road_hazard_geom()
RETURNS TRIGGER AS $$
BEGIN
    NEW.geom := ST_SetSRID(ST_Point(NEW.longitude, NEW.latitude), 4326);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_road_hazard_geom
BEFORE INSERT OR UPDATE ON m_road_hazard
FOR EACH ROW
EXECUTE FUNCTION update_road_hazard_geom();
