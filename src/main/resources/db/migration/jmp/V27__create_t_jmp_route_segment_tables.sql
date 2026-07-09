-- Description: Create JMP route segment tables to store segment details at the JMP trip plan level
CREATE TABLE IF NOT EXISTS t_jmp_route_segment (
    id VARCHAR(40) PRIMARY KEY DEFAULT gen_random_uuid(),
    jmp_trip_plan_id VARCHAR(40) NOT NULL REFERENCES t_jmp_trip_plan(id) ON DELETE CASCADE,
    start_route_point_id VARCHAR(40) REFERENCES t_jmp_route_point(id) ON DELETE SET NULL,
    end_route_point_id VARCHAR(40) REFERENCES t_jmp_route_point(id) ON DELETE SET NULL,
    seqno INTEGER,
    remarks TEXT
);

CREATE TABLE IF NOT EXISTS t_jmp_route_segment_unit (
    id VARCHAR(40) PRIMARY KEY DEFAULT gen_random_uuid(),
    jmp_route_segment_id VARCHAR(40) NOT NULL REFERENCES t_jmp_route_segment(id) ON DELETE CASCADE,
    unit_type_id VARCHAR(40)
);
