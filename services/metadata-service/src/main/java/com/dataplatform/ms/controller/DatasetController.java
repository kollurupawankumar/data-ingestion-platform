package com.dataplatform.ms.controller;


import com.dataplatform.ms.domain.Dataset;
import com.dataplatform.ms.dto.DatasetRequest;
import com.dataplatform.ms.dto.DatasetResponse;
import com.dataplatform.ms.service.DatasetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/subject-areas/{subjectAreaId}/datasets")
public class DatasetController {

    private final DatasetService datasetService;

    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DatasetResponse create(
            @PathVariable UUID subjectAreaId,
            @RequestBody DatasetRequest request
    ) {

        Dataset dataset = datasetService.create(
                subjectAreaId,
                request.getName(),
                request.getDescription()
        );

        return toResponse(dataset);
    }

    @GetMapping
    public List<DatasetResponse> getAll(@PathVariable UUID subjectAreaId) {
        return datasetService.getBySubjectArea(subjectAreaId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private DatasetResponse toResponse(Dataset d) {
        DatasetResponse res = new DatasetResponse();
        res.setId(d.getId());
        res.setName(d.getName());
        res.setDescription(d.getDescription());
        res.setEnabled(d.isEnabled());
        res.setSubjectAreaId(d.getSubjectArea().getId());
        return res;
    }
}
