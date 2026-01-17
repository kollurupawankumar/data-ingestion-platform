-- =========================
-- PIPELINE RUN TRACKING
-- =========================
CREATE TABLE IF NOT EXISTS pipeline_run (
    run_id           VARCHAR(100) PRIMARY KEY,
    dataset          VARCHAR(100) NOT NULL,
    load_type        VARCHAR(50)  NOT NULL,   -- FULL / INCREMENTAL
    status           VARCHAR(50)  NOT NULL,   -- STARTED / INGESTED / TRANSFORMED / ENRICHED / FAILED
    raw_location     TEXT,
    silver_location  TEXT,
    gold_location    TEXT,
    started_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ended_at         TIMESTAMP
);

-- =========================
-- INGESTION EVENTS STATE
-- =========================
CREATE TABLE IF NOT EXISTS ingestion_event_log (
    id               BIGSERIAL PRIMARY KEY,
    run_id           VARCHAR(100) NOT NULL,
    event_type       VARCHAR(100) NOT NULL,   -- INGESTION_REQUESTED / INGESTION_COMPLETED
    payload          JSONB NOT NULL,
    processed        BOOLEAN DEFAULT FALSE,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- CHECKPOINTS
-- =========================
CREATE TABLE IF NOT EXISTS ingestion_checkpoint (
    dataset          VARCHAR(100) PRIMARY KEY,
    checkpoint_value VARCHAR(255),
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- SCHEMA REGISTRY
-- =========================
CREATE TABLE IF NOT EXISTS dataset_schema (
    id            BIGSERIAL PRIMARY KEY,
    dataset       VARCHAR(100) NOT NULL,
    version       INT NOT NULL,
    schema_json   JSONB NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(dataset, version)
);

-- =========================
-- INDEXES
-- =========================
CREATE INDEX IF NOT EXISTS idx_pipeline_run_dataset
    ON pipeline_run(dataset);

CREATE INDEX IF NOT EXISTS idx_ingestion_event_run
    ON ingestion_event_log(run_id);
