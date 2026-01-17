package com.dataplatform.os.kafka.consumer;

import com.datafabric.common.events.PipelineDeadLetterEvent;
import com.datafabric.common.events.PipelineStageFailedEvent;
import com.datafabric.common.events.PipelineStageStartEvent;
import com.dataplatform.os.dto.RetryDecision;
import com.dataplatform.os.dto.RetryPlan;
import com.dataplatform.os.kafka.producer.PipelineEventPublisher;
import com.dataplatform.os.service.ExecutionService;
import com.dataplatform.os.service.RetryService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class StageFailedListener {

    private final RetryService retryService;
    private final PipelineEventPublisher publisher;
    private final ExecutionService executionService;
    private final TaskScheduler taskScheduler;

    public StageFailedListener(RetryService retryService, PipelineEventPublisher publisher,
                               ExecutionService executionService, TaskScheduler taskScheduler) {
        this.retryService = retryService;
        this.publisher = publisher;
        this.executionService = executionService;
        this.taskScheduler = taskScheduler;
    }

    @KafkaListener(
            topics = "${orchestrator.kafka.topics.stage-failed}",
            groupId = "orchestrator-group"
    )
    public void onStageFailed(PipelineStageFailedEvent event,
                              Acknowledgment ack) {

        RetryPlan plan =
                retryService.decideRetry(
                        event.getExecutionId(),
                        event.getStage(),
                        event.getErrorType()
                );

        if (plan.shouldRetry()) {

            executionService.incrementRetry(
                    event.getExecutionId(),
                    event.getStage()
            );

            Runnable retryTask = () -> publisher.publishStageRetry(
                    new PipelineStageStartEvent(
                            event.getExecutionId(),
                            null,
                            event.getStage(),
                            Instant.now()
                    )
            );

            taskScheduler.schedule(
                    retryTask,
                    Instant.now().plus(plan.getDelay())
            );

        } else {

            publisher.publishDeadLetter(
                    new PipelineDeadLetterEvent(
                            event.getExecutionId(),
                            event.getStage(),
                            event.getErrorMessage(),
                            Instant.now()
                    )
            );

            executionService.markExecutionFailed(
                    event.getExecutionId(),
                    event.getErrorMessage()
            );
        }

        ack.acknowledge();
    }
}