package com.dataplatform.os.dto;

import java.util.UUID;

public class PipelineExecutionResponse {

    private UUID executionId;
    private String status;

    public PipelineExecutionResponse(UUID executionId, String status) {
        this.executionId = executionId;
        this.status = status;
    }

    public UUID getExecutionId() {
        return executionId;
    }

    public String getStatus() {
        return status;
    }
}
