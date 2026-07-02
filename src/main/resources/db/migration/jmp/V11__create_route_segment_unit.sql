CREATE TABLE IF NOT EXISTS m_route_segment_unit (
    id VARCHAR(40) PRIMARY KEY DEFAULT gen_random_uuid(),
    route_segment_id VARCHAR NOT NULL,
    unit_type_id VARCHAR NOT NULL,

    CONSTRAINT fk_route_segment_unit_segment
        FOREIGN KEY (route_segment_id)
        REFERENCES m_route_segment(id)
);

CREATE INDEX IF NOT EXISTS idx_route_segment_unit_segment
ON m_route_segment_unit(route_segment_id);

CREATE INDEX IF NOT EXISTS idx_route_segment_unit
ON m_route_segment_unit(unit_type_id);