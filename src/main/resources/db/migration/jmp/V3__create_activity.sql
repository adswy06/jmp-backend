CREATE TABLE IF NOT EXISTS m_activity (
    id VARCHAR(40) PRIMARY KEY DEFAULT gen_random_uuid(),
    category_name VARCHAR(100) NOT NULL,
    name VARCHAR(200) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_activity_category
ON m_activity(category_name);

CREATE INDEX IF NOT EXISTS idx_activity_name
ON m_activity(name);