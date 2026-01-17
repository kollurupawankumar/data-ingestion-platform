package com.dataplatform.os.kafka.consumer;

import com.datafabric.common.events.PipelineStageCompletedEvent;
import com.dataplatform.os.service.OrchestrationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class StageCompletedListener {

    private final OrchestrationService orchestrationService;

    public StageCompletedListener(OrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @KafkaListener(
            topics = "${orchestrator.kafka.topics.stage-complete}",
            groupId = "orchestrator-group"
    )
    public void onStageCompleted(PipelineStageCompletedEvent event,
                                 Acknowledgment ack) {

        if (event.isSuccess()) {
            orchestrationService.handleStageSuccess(
                    event.getExecutionId(),
                    event.getStage()
            );
        } else {
            orchestrationService.handleStageFailure(
                    event.getExecutionId(),
                    event.getErrorMessage()
            );
        }

        ack.acknowledge();
    }
}

