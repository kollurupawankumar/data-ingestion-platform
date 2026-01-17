package com.dataplatform.ms.controller;


import com.dataplatform.ms.domain.TransformMetadata;
import com.dataplatform.ms.service.TransformMetadataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/datasets/{datasetId}/transforms")
public class TransformMetadataController {

    private final TransformMetadataService service;

    public TransformMetadataController(TransformMetadataService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TransformMetadata> addTransform(
            @PathVariable Long datasetId,
            @RequestParam Integer stepOrder,
            @RequestParam String ruleType,
            @RequestBody String ruleConfig) {
        return ResponseEntity.ok(service.addTransform(datasetId, stepOrder, ruleType, ruleConfig));
    }

    @GetMapping
    public ResponseEntity<List<TransformMetadata>> getTransforms(@PathVariable Long datasetId) {
        return ResponseEntity.ok(service.getTransforms(datasetId));
    }
}
