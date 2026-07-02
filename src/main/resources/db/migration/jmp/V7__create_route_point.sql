CREATE TABLE m_route_point (
    id VARCHAR(40) PRIMARY KEY DEFAULT gen_random_uuid(),
    ref_id_route VARCHAR NOT NULL,
    checksum VARCHAR(255),
    poi_id VARCHAR NOT NULL,
    seqno INTEGER,
    alias VARCHAR(255),
    address TEXT,
    paths JSONB,
    iszone BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_route_point_route
        FOREIGN KEY (ref_id_route)
        REFERENCES m_route(id),

    CONSTRAINT fk_route_point_m_poi
        FOREIGN KEY (poi_id)
        REFERENCES m_poi(id)
);

CREATE INDEX idx_route_point_route
ON m_route_point(ref_id_route);

CREATE INDEX idx_route_point_m_poi
ON m_route_point(poi_id);

CREATE INDEX idx_route_point_checksum
ON m_route_point(checksum);