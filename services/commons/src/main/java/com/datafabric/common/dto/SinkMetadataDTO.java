package com.datafabric.common.dto;


import com.fasterxml.jackson.annotation.JsonRawValue;

public class SinkMetadataDTO {
    private String sinkType;
    @JsonRawValue
    private String sinkConfig;

    public String getSinkType() {
        return sinkType;
    }

    public void setSinkType(String sinkType) {
        this.sinkType = sinkType;
    }

    public String getSinkConfig() {
        return sinkConfig;
    }

    public void setSinkConfig(String sinkConfig) {
        this.sinkConfig = sinkConfig;
    }
}

