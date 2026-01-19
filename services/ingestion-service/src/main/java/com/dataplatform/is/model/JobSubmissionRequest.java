package com.dataplatform.is.model;

import java.util.Map;


public class JobSubmissionRequest {

    // ---- Identity & Correlation ----
    private String jobName;           // spark-job-ingestion
    private String jobPath;           // .py or .jar
    private String pipelineRunId;

    // ---- Dataset Context ----
    private String dataset;
    private SourceType sourceType;    // FILE | DB | API

    // ---- Source-Specific Config ----
    private Map<String, String> sourceConfig;

    // ---- Spark Execution ----
    private SparkExecutionMode executionMode;
    private Map<String, String> sparkConf;

    // ---- Observability ----
    private String submittedBy;

    // ---- Safety ----
    private Integer timeoutSeconds;


    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobPath() {
        return jobPath;
    }

    public void setJobPath(String jobPath) {
        this.jobPath = jobPath;
    }

    public String getPipelineRunId() {
        return pipelineRunId;
    }

    public void setPipelineRunId(String pipelineRunId) {
        this.pipelineRunId = pipelineRunId;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public Map<String, String> getSourceConfig() {
        return sourceConfig;
    }

    public void setSourceConfig(Map<String, String> sourceConfig) {
        this.sourceConfig = sourceConfig;
    }

    public SparkExecutionMode getExecutionMode() {
        return executionMode;
    }

    public void setExecutionMode(SparkExecutionMode executionMode) {
        this.executionMode = executionMode;
    }

    public Map<String, String> getSparkConf() {
        return sparkConf;
    }

    public void setSparkConf(Map<String, String> sparkConf) {
        this.sparkConf = sparkConf;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }
}
