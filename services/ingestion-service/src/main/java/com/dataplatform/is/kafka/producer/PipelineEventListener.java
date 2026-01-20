package com.dataplatform.is.kafka.producer;


import com.dataplatform.is.model.IngestionRequestEvent;
import com.dataplatform.is.model.PipelineEvent;
import com.dataplatform.is.service.IngestionCompletionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class PipelineEventListener {

    private final IngestionCompletionHandler handler;

    public PipelineEventListener(IngestionCompletionHandler handler) {
        this.handler = handler;
    }

    @KafkaListener(topics = "pipeline-events",
            groupId = "ingestion-service",
            containerFactory = "kafkaListenerContainerFactory")
    public void listen(String message, Acknowledgment ack) {
        try {
            System.out.println("Billa Came here for Consumption");
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            PipelineEvent event =
                    mapper.readValue(message, PipelineEvent.class);
            System.out.println(event.toString());
            handler.handle(event);
            ack.acknowledge();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
