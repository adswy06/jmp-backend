CREATE TABLE m_activity_cost (
    id VARCHAR(40) PRIMARY KEY DEFAULT gen_random_uuid(),
    route_point_id VARCHAR NOT NULL,
    activity_id VARCHAR NOT NULL,
    amount DOUBLE PRECISION,

    CONSTRAINT fk_activity_cost_route_point
        FOREIGN KEY (route_point_id)
        REFERENCES m_route_point(id),

    CONSTRAINT fk_activity_cost_activity
        FOREIGN KEY (activity_id)
        REFERENCES m_activity(id)
);

CREATE INDEX idx_activity_cost_route_point
ON m_activity_cost(route_point_id);

CREATE INDEX idx_activity_cost_activity
ON m_activity_cost(activity_id);