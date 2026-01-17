package com.datafabric.common.events;


import java.time.Instant;
import java.util.UUID;

public class PipelineDeadLetterEvent {

    private UUID executionId;
    private String stage;
    private String errorMessage;
    private Instant failedAt;

    public PipelineDeadLetterEvent(UUID executionId, String stage, String errorMessage, Instant failedAt) {
        this.executionId = executionId;
        this.stage = stage;
        this.errorMessage = errorMessage;
        this.failedAt = failedAt;
    }

    public UUID getExecutionId() {
        return executionId;
    }

    public void setExecutionId(UUID executionId) {
        this.executionId = executionId;
    }

    public Instant getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(Instant failedAt) {
        this.failedAt = failedAt;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
