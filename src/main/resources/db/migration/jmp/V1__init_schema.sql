CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE OR REPLACE FUNCTION public.get_uuid()
RETURNS VARCHAR AS
$$
BEGIN
    RETURN REPLACE(gen_random_uuid()::varchar, '-', '');
END;
$$
LANGUAGE plpgsql;

-- ==========================================
-- TABLE: m_route
-- ==========================================
CREATE TABLE m_route (
    id VARCHAR(40) PRIMARY KEY NOT NULL DEFAULT public.get_uuid(),
    alias VARCHAR(255) NOT NULL,
    location_from_id UUID,
    location_to_id UUID,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    is_delete BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(255),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(255)
);

-- ==========================================
-- TABLE: m_route_detail
-- ==========================================
CREATE TABLE m_route_detail (
    id VARCHAR(40) PRIMARY KEY NOT NULL DEFAULT public.get_uuid(),
    ref_id_route VARCHAR(40) NOT NULL REFERENCES m_route(id),
    address VARCHAR(500),
    sequence DOUBLE PRECISION NOT NULL,
    lat DOUBLE PRECISION NOT NULL,
    lng DOUBLE PRECISION NOT NULL,
    distance DOUBLE PRECISION,
    geom GEOGRAPHY,
    is_delete BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    created_by VARCHAR(255),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100)
);

CREATE INDEX idx_route_detail_route
    ON m_route_detail(ref_id_route);

-- ==========================================
-- TABLE: m_route_segment
-- ==========================================
CREATE TABLE m_route_segment (
    id VARCHAR(40) PRIMARY KEY NOT NULL DEFAULT public.get_uuid(),
    ref_id_route_detail VARCHAR(40) NOT NULL REFERENCES m_route_detail(id),
    sequence_no INTEGER NOT NULL,
    address VARCHAR(255),
    distance DOUBLE PRECISION,
    duration DOUBLE PRECISION,
    instructions VARCHAR(1000),
    description VARCHAR(1000),
    segment_geom GEOGRAPHY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    created_by VARCHAR(255),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(255),
    is_delete BOOLEAN DEFAULT FALSE
);

CREATE INDEX idx_route_segment_route_detail
    ON m_route_segment(ref_id_route_detail);