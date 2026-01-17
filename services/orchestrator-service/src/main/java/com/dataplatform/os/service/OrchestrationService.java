package com.dataplatform.os.service;

import com.datafabric.common.dto.PipelineMetadataDTO;
import com.datafabric.common.events.PipelineStageStartEvent;
import com.dataplatform.os.client.MetadataServiceClient;
import com.dataplatform.os.dto.PipelineExecutionResponse;
import com.dataplatform.os.entity.JobExecution;

import com.dataplatform.os.kafka.producer.PipelineEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrchestrationService {

    private final MetadataServiceClient metadataServiceClient;
    private final ExecutionService executionService;
    private final StateTransitionService stateTransitionService;
    private final PipelineEventPublisher publisher;

    public OrchestrationService(MetadataServiceClient metadataServiceClient,
                                ExecutionService executionService,
                                StateTransitionService stateTransitionService,
                                PipelineEventPublisher publisher) {
        this.metadataServiceClient = metadataServiceClient;
        this.executionService = executionService;
        this.stateTransitionService = stateTransitionService;
        this.publisher = publisher;
    }

    public PipelineExecutionResponse startPipeline(Long datasetId) {

        // 1. Validate metadata exists
        PipelineMetadataDTO metadata =
                metadataServiceClient.getPipelineMetadata(datasetId);

        // 2. Start execution
        JobExecution jobExecution =
                executionService.startExecution(datasetId);

        // 3. Mark first stage started
        executionService.markStageStarted(
                jobExecution.getExecutionId(),
                "INGESTION"
        );

        // Kafka event will be published here next
        publisher.publishStageStart(
                new PipelineStageStartEvent(
                        jobExecution.getExecutionId(),
                        datasetId,
                        "INGESTION",
                        Instant.now()
                )
        );

        return new PipelineExecutionResponse(
                jobExecution.getExecutionId(),
                jobExecution.getStatus()
        );
    }



    public void handleStageSuccess(UUID executionId, String completedStage) {

        executionService.markStageCompleted(executionId, completedStage);

        Optional<String> nextStage =
                stateTransitionService.nextStage(completedStage);

        if (nextStage.isPresent()) {
            executionService.markStageStarted(executionId, nextStage.get());
            // Kafka event will be published here
            publisher.publishStageStart(
                    new PipelineStageStartEvent(
                            executionId,
                            null,
                            nextStage.get(),
                            Instant.now()
                    )
            );
        } else {
            executionService.markExecutionSuccess(executionId);
        }
    }

    public void handleStageFailure(UUID executionId, String errorMessage) {
        executionService.markExecutionFailed(executionId, errorMessage);
    }
}

