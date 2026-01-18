package com.dataplatform.is.model;

import java.time.Instant;
import java.time.LocalDateTime;

public class PipelineEvent {

    private String runId;
    private String dataset;
    private String loadType;
    private String status;
    private String rawLocation;
    private String silverLocation;
    private String goldLocation;
    private String errorCode;
    private String errorMessage;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime lastUpdatedAt;

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public String getLoadType() {
        return loadType;
    }

    public void setLoadType(String loadType) {
        this.loadType = loadType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRawLocation() {
        return rawLocation;
    }

    public void setRawLocation(String rawLocation) {
        this.rawLocation = rawLocation;
    }

    public String getSilverLocation() {
        return silverLocation;
    }

    public void setSilverLocation(String silverLocation) {
        this.silverLocation = silverLocation;
    }

    public String getGoldLocation() {
        return goldLocation;
    }

    public void setGoldLocation(String goldLocation) {
        this.goldLocation = goldLocation;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    @Override
    public String toString() {
        return "PipelineEvent{" +
                "runId='" + runId + '\'' +
                ", dataset='" + dataset + '\'' +
                ", loadType='" + loadType + '\'' +
                ", status='" + status + '\'' +
                ", rawLocation='" + rawLocation + '\'' +
                ", silverLocation='" + silverLocation + '\'' +
                ", goldLocation='" + goldLocation + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", startedAt=" + startedAt +
                ", endedAt=" + endedAt +
                ", lastUpdatedAt=" + lastUpdatedAt +
                '}';
    }
}
