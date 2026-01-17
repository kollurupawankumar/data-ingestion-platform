package com.dataplatform.os.controller;

import com.dataplatform.os.dto.PipelineExecutionResponse;
import com.dataplatform.os.service.OrchestrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/pipelines")
public class OrchestratorController {

    private final OrchestrationService orchestrationService;

    public OrchestratorController(OrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @GetMapping("/{datasetId}/run")
    public ResponseEntity<PipelineExecutionResponse> runPipeline(
            @PathVariable Long datasetId
    ) {
        PipelineExecutionResponse response =
                orchestrationService.startPipeline(datasetId);

        return ResponseEntity.ok(response);
    }
}
