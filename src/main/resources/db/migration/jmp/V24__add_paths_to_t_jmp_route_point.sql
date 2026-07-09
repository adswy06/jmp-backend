-- Migration: V24__add_paths_to_t_jmp_route_point.sql
-- Description: Add paths JSON column to t_jmp_route_point table to align with m_route_point.

ALTER TABLE t_jmp_route_point ADD COLUMN IF NOT EXISTS paths JSON;
