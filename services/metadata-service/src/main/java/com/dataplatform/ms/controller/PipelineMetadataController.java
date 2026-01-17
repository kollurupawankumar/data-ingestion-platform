package com.dataplatform.ms.controller;

import com.dataplatform.ms.dto.PipelineMetadata;
import com.dataplatform.ms.service.PipelineMetadataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/metadata/pipelines")
public class PipelineMetadataController {

    private final PipelineMetadataService service;

    public PipelineMetadataController(PipelineMetadataService service) {
        this.service = service;
    }

    @GetMapping("/{datasetId}")
    public ResponseEntity<PipelineMetadata> getPipeline(
            @PathVariable Long datasetId) {
        System.out.println("Came here billa");
        PipelineMetadata details = service.getPipeline(datasetId);
        System.out.println(details);
        return ResponseEntity.ok(details);
    }
}

