CREATE TABLE IF NOT EXISTS t_jmp (
    id VARCHAR(40) PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id VARCHAR(40),
    consignee_id VARCHAR(40),
    commercial_route VARCHAR(255),
    reference_no VARCHAR(100),
    title VARCHAR(255),
    description TEXT,
    status VARCHAR(50),
    createdat TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    createdby VARCHAR(100),
    updatedat TIMESTAMP,
    updatedby VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS t_jmp_trip_plan (
    id VARCHAR(40) PRIMARY KEY DEFAULT gen_random_uuid(),
    jmp_id VARCHAR(40) NOT NULL REFERENCES t_jmp(id) ON DELETE CASCADE,
    route_id VARCHAR(40) REFERENCES m_route(id),
    seqno INTEGER,
    transport_mode VARCHAR(50),
    remarks TEXT,
    createdat TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS t_jmp_route_point (
    id VARCHAR(40) PRIMARY KEY DEFAULT gen_random_uuid(),
    jmp_trip_plan_id VARCHAR(40) NOT NULL REFERENCES t_jmp_trip_plan(id) ON DELETE CASCADE,
    route_point_id VARCHAR(40) REFERENCES m_route_point(id),
    poi_id VARCHAR(40) NOT NULL REFERENCES m_poi(id),
    seqno INTEGER,
    alias VARCHAR(100),
    address TEXT,
    iscustom BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS t_jmp_activity (
    id VARCHAR(40) PRIMARY KEY DEFAULT gen_random_uuid(),
    jmp_route_point_id VARCHAR(40) NOT NULL REFERENCES t_jmp_route_point(id) ON DELETE CASCADE,
    activity_id VARCHAR(40) REFERENCES m_activity(id),
    activity_name VARCHAR(255),
    leadtime INTEGER,
    cost DECIMAL(19,4),
    seqno INTEGER,
    remarks TEXT
);

CREATE TABLE IF NOT EXISTS t_jmp_extra_cost (
    id VARCHAR(40) PRIMARY KEY DEFAULT gen_random_uuid(),
    jmp_trip_plan_id VARCHAR(40) NOT NULL REFERENCES t_jmp_trip_plan(id) ON DELETE CASCADE,
    name VARCHAR(255),
    amount DECIMAL(19,4),
    remarks TEXT
);

CREATE TABLE IF NOT EXISTS t_jmp_driver (
    id VARCHAR(40) PRIMARY KEY DEFAULT gen_random_uuid(),
    jmp_trip_plan_id VARCHAR(40) NOT NULL REFERENCES t_jmp_trip_plan(id) ON DELETE CASCADE,
    driver_id VARCHAR(100),
    seqno INTEGER
);

CREATE TABLE IF NOT EXISTS t_jmp_unit (
    id VARCHAR(40) PRIMARY KEY DEFAULT gen_random_uuid(),
    jmp_trip_plan_id VARCHAR(40) NOT NULL REFERENCES t_jmp_trip_plan(id) ON DELETE CASCADE,
    unit_type_id VARCHAR(40)
);

CREATE TABLE IF NOT EXISTS t_jmp_sea (
    id VARCHAR(40) PRIMARY KEY DEFAULT gen_random_uuid(),
    jmp_trip_plan_id VARCHAR(40) NOT NULL REFERENCES t_jmp_trip_plan(id) ON DELETE CASCADE,
    origin_port_id VARCHAR(40),
    destination_port_id VARCHAR(40),
    vessel_id VARCHAR(40),
    etd TIMESTAMP,
    eta TIMESTAMP
);

CREATE TABLE IF NOT EXISTS t_jmp_air (
    id VARCHAR(40) PRIMARY KEY DEFAULT gen_random_uuid(),
    jmp_trip_plan_id VARCHAR(40) NOT NULL REFERENCES t_jmp_trip_plan(id) ON DELETE CASCADE,
    origin_airport_id VARCHAR(40),
    destination_airport_id VARCHAR(40),
    airline VARCHAR(255),
    flight_no VARCHAR(100),
    etd TIMESTAMP,
    eta TIMESTAMP
);

-- Indices for performance
CREATE INDEX IF NOT EXISTS idx_t_jmp_trip_plan_jmp ON t_jmp_trip_plan(jmp_id);
CREATE INDEX IF NOT EXISTS idx_t_jmp_route_point_plan ON t_jmp_route_point(jmp_trip_plan_id);
CREATE INDEX IF NOT EXISTS idx_t_jmp_activity_point ON t_jmp_activity(jmp_route_point_id);
