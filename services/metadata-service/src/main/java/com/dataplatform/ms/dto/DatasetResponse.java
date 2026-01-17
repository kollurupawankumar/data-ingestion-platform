package com.dataplatform.ms.dto;

import java.util.UUID;

public class DatasetResponse {

    private Long id;
    private String name;
    private String description;
    private int version;
    private boolean enabled;
    private UUID subjectAreaId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public UUID getSubjectAreaId() {
        return subjectAreaId;
    }

    public void setSubjectAreaId(UUID subjectAreaId) {
        this.subjectAreaId = subjectAreaId;
    }
}