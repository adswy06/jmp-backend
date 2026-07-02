CREATE TABLE IF NOT EXISTS m_route (
    id VARCHAR(40) PRIMARY KEY DEFAULT gen_random_uuid(),
    alias VARCHAR(100),
    name VARCHAR(200) NOT NULL,
    isactive BOOLEAN NOT NULL DEFAULT TRUE,
    isdeleted BOOLEAN NOT NULL DEFAULT FALSE,
    deletedat TIMESTAMP,
    createdby VARCHAR(100),
    createdat TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedby VARCHAR(100),
    updatedat TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_route_name
ON m_route(name);

CREATE INDEX IF NOT EXISTS idx_route_active
ON m_route(isactive);

CREATE INDEX IF NOT EXISTS idx_route_deleted
ON m_route(isdeleted);