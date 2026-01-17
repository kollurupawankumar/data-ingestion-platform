package com.dataplatform.is.service;

import com.dataplatform.is.model.PipelineEvent;
import org.springframework.stereotype.Service;

@Service
public class IngestionCompletionHandler {

    private final PipelineRunService pipelineRunService;


    public IngestionCompletionHandler(PipelineRunService pipelineRunService) {
        this.pipelineRunService = pipelineRunService;

    }

    public void handle(PipelineEvent event) {

        if (!"INGESTION_JOB_COMPLETED".equals(event.getEventType())) {
            return; // ignore others
        }

        if ("SUCCESS".equals(event.getStatus())) {
            // 1. Mark INGESTED
            pipelineRunService.markIngested(event.getPipelineRunId());
        } else {
            pipelineRunService.markFailed(event.getPipelineRunId());
        }
    }
}

