-- Description: Add status column to m_route table to support draft routes.
ALTER TABLE m_route ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED';
