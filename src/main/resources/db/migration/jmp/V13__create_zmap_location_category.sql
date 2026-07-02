CREATE TABLE IF NOT EXISTS zmap_location_category (
    id VARCHAR PRIMARY KEY DEFAULT gen_random_uuid(),
    location_category_id VARCHAR NOT NULL REFERENCES m_location_category(id),
    target_app VARCHAR(100),
    target_id VARCHAR,
    priority INTEGER,
    identifier1 VARCHAR(100),
    identifier2 VARCHAR(100),
    identifier3 VARCHAR(100),
    identifier4 VARCHAR(100),
    identifier5 VARCHAR(100),
    last_used TIMESTAMP,
    usage_count INTEGER DEFAULT 0,
    usage_updated TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_zmap_location_category
ON zmap_location_category(location_category_id);

CREATE INDEX IF NOT EXISTS idx_zmap_target
ON zmap_location_category(target_app, target_id);