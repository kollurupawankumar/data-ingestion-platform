--CREATE TABLE subject_area (
--    id BIGSERIAL PRIMARY KEY,
--    name VARCHAR(100) NOT NULL UNIQUE,
--    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
--);

CREATE TABLE  subject_area (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);


CREATE TABLE dataset (
    id BIGSERIAL PRIMARY KEY,
    subject_area_id UUID NOT NULL,
    entity_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    enabled BOOLEAN DEFAULT TRUE,
    load_type VARCHAR(50),        -- FULL / INCREMENTAL
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_entity_subject
        FOREIGN KEY (subject_area_id) REFERENCES subject_area(id),
    CONSTRAINT uq_entity UNIQUE (subject_area_id, entity_name)
);


CREATE TABLE source_metadata (
    id BIGSERIAL PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    source_type VARCHAR(50) NOT NULL,   -- FILE / DB / API / KAFKA
    source_config JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_source_entity
        FOREIGN KEY (entity_id) REFERENCES Dataset(id)
);


CREATE TABLE transform_metadata (
    id BIGSERIAL PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    step_order INT NOT NULL,
    rule_type VARCHAR(50) NOT NULL,
    rule_config JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transform_entity
        FOREIGN KEY (entity_id) REFERENCES Dataset(id)
);


CREATE TABLE enrichment_metadata (
    id BIGSERIAL PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    step_order INT NOT NULL,
    rule_type VARCHAR(50) NOT NULL,
    rule_config JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_enrich_entity
        FOREIGN KEY (entity_id) REFERENCES Dataset(id)
);


CREATE TABLE sink_metadata (
    id BIGSERIAL PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    sink_type VARCHAR(50) NOT NULL,   -- POSTGRES / HIVE / S3
    sink_config JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sink_entity
        FOREIGN KEY (entity_id) REFERENCES Dataset(id)
);


CREATE TABLE job_execution (
    execution_id UUID PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,   -- STARTED / SUCCESS / FAILED
    records_processed BIGINT,
    error_message TEXT,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    CONSTRAINT fk_job_entity
        FOREIGN KEY (entity_id) REFERENCES Dataset(id)
);
