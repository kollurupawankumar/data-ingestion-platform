package com.datafabric.common.events;


import java.util.UUID;

public class PipelineStageCompletedEvent {

    private UUID executionId;
    private String stage;
    private boolean success;
    private String errorMessage;

    public PipelineStageCompletedEvent(UUID executionId, String stage, boolean success, String errorMessage) {
        this.executionId = executionId;
        this.stage = stage;
        this.success = success;
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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

