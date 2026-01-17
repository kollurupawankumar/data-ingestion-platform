package com.dataplatform.os.kafka.producer;

import com.datafabric.common.events.PipelineDeadLetterEvent;
import com.datafabric.common.events.PipelineStageStartEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PipelineEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PipelineEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Value("${orchestrator.kafka.topics.stage-start}")
    private String stageStartTopic;

    @Value("${orchestrator.kafka.topics.stage-retry}")
    private String stageRetryTopic;

    @Value("${orchestrator.kafka.topics.stage-dlq}")
    private String stageDlqTopic;

    public void publishStageStart(PipelineStageStartEvent event) {
        kafkaTemplate.send(stageStartTopic, key(event), event);
    }

    public void publishStageRetry(PipelineStageStartEvent event) {
        kafkaTemplate.send(stageRetryTopic, key(event), event);
    }

    public void publishDeadLetter(PipelineDeadLetterEvent event) {
        kafkaTemplate.send(stageDlqTopic, key(event), event);
    }

    private String key(Object event) {
        if (event instanceof PipelineStageStartEvent e)
            return e.getExecutionId().toString();
        if (event instanceof PipelineDeadLetterEvent e)
            return e.getExecutionId().toString();
        return null;
    }
}

