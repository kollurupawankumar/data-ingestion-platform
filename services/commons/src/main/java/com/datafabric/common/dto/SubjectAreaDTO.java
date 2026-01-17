package com.datafabric.common.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class SubjectAreaDTO {
    private String name;
    private String description;

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
}
