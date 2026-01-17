package com.dataplatform.is.kafka.producer;


import com.dataplatform.is.model.PipelineEvent;
import com.dataplatform.is.service.IngestionCompletionHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PipelineEventListener {

    private final IngestionCompletionHandler handler;

    public PipelineEventListener(IngestionCompletionHandler handler) {
        this.handler = handler;
    }

    @KafkaListener(topics = "pipeline-events", groupId = "ingestion-service")
    public void listen(PipelineEvent event) {
        handler.handle(event);
    }
}
