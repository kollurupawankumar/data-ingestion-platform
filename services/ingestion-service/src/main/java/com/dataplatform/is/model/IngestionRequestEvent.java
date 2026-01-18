package com.dataplatform.is.model;


import java.util.Map;

public class IngestionRequestEvent {

    private String dataset;
    private String sourceType; // FILE | DB | API
    private String pipelineRunId;
    private Map<String, String> params;

    public String getDataset() { return dataset; }
    public String getSourceType() { return sourceType; }

    public String getPipelineRunId() {
        return pipelineRunId;
    }

    public Map<String, String> getParams() { return params; }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public void setPipelineRunId(String pipelineRunId) {
        this.pipelineRunId = pipelineRunId;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
