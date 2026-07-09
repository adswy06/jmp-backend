-- Description: Alter t_jmp and t_jmp_activity, and migrate t_jmp_unit relation to t_jmp (JMP level)
ALTER TABLE t_jmp ADD COLUMN is_notification_global BOOLEAN DEFAULT FALSE;

ALTER TABLE t_jmp_unit DROP COLUMN IF EXISTS jmp_trip_plan_id;
ALTER TABLE t_jmp_unit ADD COLUMN jmp_id VARCHAR(40) REFERENCES t_jmp(id) ON DELETE CASCADE;

ALTER TABLE t_jmp_activity ADD COLUMN is_notification BOOLEAN DEFAULT FALSE;
ALTER TABLE t_jmp_activity ADD COLUMN notes TEXT;
