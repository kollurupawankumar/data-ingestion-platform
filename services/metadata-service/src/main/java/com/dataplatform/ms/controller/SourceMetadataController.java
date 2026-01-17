package com.dataplatform.ms.controller;

import com.dataplatform.ms.domain.SourceMetadata;
import com.dataplatform.ms.service.SourceMetadataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/entities/{datasetId}/source")
public class SourceMetadataController {

    private final SourceMetadataService service;

    public SourceMetadataController(SourceMetadataService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SourceMetadata> createSource(
            @PathVariable Long datasetId,
            @RequestParam SourceMetadata.SourceType type,
            @RequestBody String config) {
        return ResponseEntity.ok(service.createSource(datasetId, type, config));
    }

    @GetMapping
    public ResponseEntity<SourceMetadata> getSource(@PathVariable Long datasetId) {
        return service.getByDataset(datasetId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<SourceMetadata>> getAllSources(@PathVariable Long datasetId) {
        return ResponseEntity.ok(service.getAllSources(datasetId));
    }
}
