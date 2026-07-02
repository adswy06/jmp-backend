CREATE TABLE IF NOT EXISTS m_location_category (
    id VARCHAR(40) PRIMARY KEY DEFAULT gen_random_uuid(),
    category_name VARCHAR(100) NOT NULL,
    description TEXT,
    lastsync TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_m_location_category_name
ON m_location_category(category_name);