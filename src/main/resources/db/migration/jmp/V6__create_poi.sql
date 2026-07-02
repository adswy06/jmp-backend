CREATE TABLE m_poi (
    id VARCHAR(40) PRIMARY KEY DEFAULT gen_random_uuid(),
    gpoi_id VARCHAR(100),
    zone_id VARCHAR,
    location_category VARCHAR(100),
    name VARCHAR(255) NOT NULL,
    address TEXT,
    lng DOUBLE PRECISION,
    lat DOUBLE PRECISION,
    isactive BOOLEAN DEFAULT TRUE,
    createdat TIMESTAMP,
    createdby VARCHAR(100),
    updatedat TIMESTAMP,
    updatedby VARCHAR(100),

    CONSTRAINT fk_m_poi_zone
        FOREIGN KEY (zone_id)
        REFERENCES m_zone(id),

    CONSTRAINT fk_m_poi_location_category
        FOREIGN KEY (location_category)
        REFERENCES m_location_category(category_name)
);

CREATE INDEX idx_m_poi_zone
ON m_poi(zone_id);

CREATE INDEX idx_m_poi_location_category
ON m_poi(location_category);

CREATE INDEX idx_m_poi_gpoi
ON m_poi(gpoi_id);