package com.datafabric.common.events;


import java.util.UUID;

public class PipelineStageFailedEvent {

    private UUID executionId;
    private String stage;
    private String errorType;     // TRANSIENT / PERMANENT
    private String errorMessage;

    public PipelineStageFailedEvent(UUID executionId, String stage, String errorType, String errorMessage) {
        this.executionId = executionId;
        this.stage = stage;
        this.errorType = errorType;
        this.errorMessage = errorMessage;
    }

    public UUID getExecutionId() {
        return executionId;
    }

    public void setExecutionId(UUID executionId) {
        this.executionId = executionId;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

