-- V2__alter_pipeline_run_add_error_and_audit_columns.sql

ALTER TABLE pipeline_run
    ADD COLUMN IF NOT EXISTS error_code VARCHAR(100);

ALTER TABLE pipeline_run
    ADD COLUMN IF NOT EXISTS error_message TEXT;

ALTER TABLE pipeline_run
    ADD COLUMN IF NOT EXISTS last_updated_at TIMESTAMP
    NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- ended_at already exists in your table,
-- so we do NOT add it again.
