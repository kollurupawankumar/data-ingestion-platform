package com.dataplatform.ms.controller;

import com.dataplatform.ms.domain.EnrichmentMetadata;
import com.dataplatform.ms.service.EnrichmentMetadataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/datasets/{datasetId}/enrichments")
public class EnrichmentMetadataController {

    private final EnrichmentMetadataService service;


    public EnrichmentMetadataController(EnrichmentMetadataService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EnrichmentMetadata> addEnrichment(
            @PathVariable Long datasetId,
            @RequestParam Integer stepOrder,
            @RequestParam String ruleType,
            @RequestBody String ruleConfig) {
        return ResponseEntity.ok(service.addEnrichment(datasetId, stepOrder, ruleType, ruleConfig));
    }

    @GetMapping
    public ResponseEntity<List<EnrichmentMetadata>> getEnrichments(@PathVariable Long datasetId) {
        return ResponseEntity.ok(service.getEnrichments(datasetId));
    }
}
