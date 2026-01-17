CREATE TABLE job_execution (
    execution_id UUID PRIMARY KEY,
    dataset_id UUID NOT NULL,

    status VARCHAR(50) NOT NULL,      -- STARTED / SUCCESS / FAILED
    current_stage VARCHAR(50),        -- INGESTION / TRANSFORMATION / ENRICHMENT / SINK

    records_processed BIGINT,
    error_message TEXT,

    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP
);


CREATE TABLE stage_execution (
    id BIGSERIAL PRIMARY KEY,
    execution_id UUID NOT NULL,
    stage_name VARCHAR(50) NOT NULL,   -- INGESTION / TRANSFORMATION / ENRICHMENT / SINK
    status VARCHAR(50) NOT NULL,       -- STARTED / SUCCESS / FAILED
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    error_message TEXT,

    CONSTRAINT fk_stage_execution
        FOREIGN KEY (execution_id)
        REFERENCES job_execution(execution_id)
);


CREATE INDEX idx_job_execution_dataset
    ON job_execution(dataset_id);

CREATE INDEX idx_stage_execution_exec
    ON stage_execution(execution_id);
