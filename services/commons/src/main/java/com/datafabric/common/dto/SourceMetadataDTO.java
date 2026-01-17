package com.datafabric.common.dto;


import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SourceMetadataDTO {
    private Long id;
    private String sourceType;
    @JsonRawValue
    private String sourceConfig; // JSON string

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceConfig() {
        return sourceConfig;
    }

    public void setSourceConfig(String sourceConfig) {
        this.sourceConfig = sourceConfig;
    }

    @JsonSetter("sourceConfig")
    public void setSourceConfigRaw(Object sourceConfig) throws JsonProcessingException {
        if (sourceConfig instanceof String) {
            this.sourceConfig = (String) sourceConfig;
        } else {
            // Convert object to JSON string
            ObjectMapper mapper = new ObjectMapper();
            this.sourceConfig = mapper.writeValueAsString(sourceConfig);
        }
    }
}
