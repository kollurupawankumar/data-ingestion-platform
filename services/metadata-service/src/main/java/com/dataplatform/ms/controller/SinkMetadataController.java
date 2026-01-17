package com.dataplatform.ms.controller;

import com.dataplatform.ms.domain.SinkMetadata;
import com.dataplatform.ms.service.SinkMetadataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/datasets/{datasetId}/sink")
public class SinkMetadataController {

    private final SinkMetadataService service;

    public SinkMetadataController(SinkMetadataService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SinkMetadata> createSink(
            @PathVariable Long datasetId,
            @RequestParam SinkMetadata.SinkType type,
            @RequestBody String config) {
        return ResponseEntity.ok(service.createSink(datasetId, type, config));
    }

    @GetMapping
    public ResponseEntity<SinkMetadata> getSink(@PathVariable Long datasetId) {
        return service.getByDataset(datasetId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<SinkMetadata>> getAllSinks(@PathVariable Long datasetId) {
        return ResponseEntity.ok(service.getAllSinks(datasetId));
    }
}