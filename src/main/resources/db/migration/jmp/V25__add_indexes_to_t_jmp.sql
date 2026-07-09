-- Migration: V25__add_indexes_to_t_jmp.sql
-- Description: Add performance indexes to t_jmp table for Datatables sorting and filtering

CREATE INDEX IF NOT EXISTS idx_t_jmp_createdat ON t_jmp (createdat DESC);
CREATE INDEX IF NOT EXISTS idx_t_jmp_customer_id ON t_jmp (customer_id);
CREATE INDEX IF NOT EXISTS idx_t_jmp_consignee_id ON t_jmp (consignee_id);
CREATE INDEX IF NOT EXISTS idx_t_jmp_status ON t_jmp (status);
