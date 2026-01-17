package com.datafabric.common.events;


import java.util.UUID;

public class StageCompletedEvent {

    private UUID executionId;
    private Long datasetId;
    private String stage;   // INGESTION / TRANSFORM / ENRICH / SINK
    private boolean success;

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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
