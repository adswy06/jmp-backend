CREATE TABLE m_activity_lead_time (
    id VARCHAR(40) PRIMARY KEY DEFAULT gen_random_uuid(),
    route_point_id VARCHAR NOT NULL,
    activity_id VARCHAR NOT NULL,
    leadtime INTEGER,

    CONSTRAINT fk_activity_leadtime_route_point
        FOREIGN KEY (route_point_id)
        REFERENCES m_route_point(id),

    CONSTRAINT fk_activity_leadtime_activity
        FOREIGN KEY (activity_id)
        REFERENCES m_activity(id)
);

CREATE INDEX idx_activity_leadtime_route_point
ON m_activity_lead_time(route_point_id);

CREATE INDEX idx_activity_leadtime_activity
ON m_activity_lead_time(activity_id);