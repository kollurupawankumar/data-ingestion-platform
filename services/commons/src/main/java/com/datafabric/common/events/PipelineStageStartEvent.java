package com.datafabric.common.events;


import java.time.Instant;
import java.util.UUID;

public class PipelineStageStartEvent {

    private UUID executionId;
    private Long datasetId;
    private String stage;     // INGESTION / TRANSFORMATION / ENRICHMENT / SINK
    private Instant triggeredAt;

    public PipelineStageStartEvent(UUID executionId, Long datasetId, String stage, Instant triggeredAt) {
        this.executionId = executionId;
        this.datasetId = datasetId;
        this.stage = stage;
        this.triggeredAt = triggeredAt;
    }

    public UUID getExecutionId() {
        return executionId;
    }

    public void setExecutionId(UUID executionId) {
        this.executionId = executionId;
    }

    public Long getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Long datasetId) {
        this.datasetId = datasetId;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public Instant getTriggeredAt() {
        return triggeredAt;
    }

    public void setTriggeredAt(Instant triggeredAt) {
        this.triggeredAt = triggeredAt;
    }
}
