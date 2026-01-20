DROP TABLE IF EXISTS pipeline_run;
CREATE TABLE pipeline_run (
                              run_id TEXT PRIMARY KEY,
                              dataset TEXT NOT NULL,
                              load_type TEXT NOT NULL,

                              status TEXT NOT NULL,

                              started_at TIMESTAMP NOT NULL,
                              ended_at TIMESTAMP,

                              last_updated_at TIMESTAMP NOT NULL,

                              error_code TEXT,
                              error_message TEXT
);

CREATE TABLE pipeline_run_state (
                                    id BIGSERIAL PRIMARY KEY,

                                    run_id TEXT NOT NULL,
                                    state TEXT NOT NULL,

                                    state_started_at TIMESTAMP NOT NULL,
                                    state_ended_at TIMESTAMP,

                                    duration_ms BIGINT,

                                    CONSTRAINT fk_pipeline_run
                                        FOREIGN KEY (run_id)
                                            REFERENCES pipeline_run(run_id)
                                            ON DELETE CASCADE
);

-- Ensure only one active state per run
CREATE INDEX idx_pipeline_run_state_active
    ON pipeline_run_state(run_id)
    WHERE state_ended_at IS NULL;

