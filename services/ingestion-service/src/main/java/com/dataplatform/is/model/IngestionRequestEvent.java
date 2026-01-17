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
}
