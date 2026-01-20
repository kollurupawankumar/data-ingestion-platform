package com.dataplatform.is.service;

import com.dataplatform.is.model.PipelineEvent;
import com.dataplatform.is.model.PipelineState;
import org.springframework.stereotype.Service;

@Service
public class IngestionCompletionHandler {

    private final PipelineRunTrackingService pipelineRunTrackingService;


    public IngestionCompletionHandler(PipelineRunTrackingService pipelineRunTrackingService) {
        this.pipelineRunTrackingService = pipelineRunTrackingService;

    }

    public void handle(PipelineEvent event) {
        System.out.println("The pipeline event - " + event);
        switch (event.getEventType()) {
            case "INGESTION_JOB_RUNNING" -> {
                pipelineRunTrackingService.transitionState(
                        event.getPipelineRunId(),
                        PipelineState.INGESTION_RUNNING
                );
            }
            case "INGESTION_JOB_COMPLETED" -> {
                pipelineRunTrackingService.transitionState(
                        event.getPipelineRunId(),
                        PipelineState.COMPLETED
                );
            }
            case "INGESTION_JOB_FAILED" -> {
                pipelineRunTrackingService.failRun(
                        event.getPipelineRunId(),
                        event.getErrorCode(),
                        event.getErrorMessage()
                );
            }

            default -> {
                // ignore
            }
        }
    }

}

