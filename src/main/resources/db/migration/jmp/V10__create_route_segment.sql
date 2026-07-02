CREATE TABLE IF NOT EXISTS m_route_segment (
    id VARCHAR(40) PRIMARY KEY DEFAULT gen_random_uuid(),
    route_id VARCHAR NOT NULL,
    start_route_point_id VARCHAR NOT NULL,
    end_route_point_id VARCHAR NOT NULL,
    seqno INTEGER NOT NULL,
    remarks VARCHAR(500),

    CONSTRAINT fk_route_segment_route
        FOREIGN KEY (route_id)
        REFERENCES m_route(id),

    CONSTRAINT fk_route_segment_start_point
        FOREIGN KEY (start_route_point_id)
        REFERENCES m_route_point(id),

    CONSTRAINT fk_route_segment_end_point
        FOREIGN KEY (end_route_point_id)
        REFERENCES m_route_point(id)
);

CREATE INDEX IF NOT EXISTS idx_route_segment_route
ON m_route_segment(route_id);

CREATE INDEX IF NOT EXISTS idx_route_segment_start
ON m_route_segment(start_route_point_id);

CREATE INDEX IF NOT EXISTS idx_route_segment_end
ON m_route_segment(end_route_point_id);

CREATE INDEX IF NOT EXISTS idx_route_segment_seq
ON m_route_segment(route_id, seqno);