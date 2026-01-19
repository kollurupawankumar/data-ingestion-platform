package com.dataplatform.is.kafka.consumer;

import com.dataplatform.is.service.IngestionEventHandler;
import com.dataplatform.is.model.IngestionRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class IngestionKafkaListener {

    private final IngestionEventHandler sparkJobRunner;

    public IngestionKafkaListener(IngestionEventHandler sparkJobRunner) {
        this.sparkJobRunner = sparkJobRunner;
    }

    @KafkaListener(topics = "ingestion-requests" ,
                groupId = "ingestion-service",
                containerFactory = "kafkaListenerContainerFactory")
    public void listen(String message, Acknowledgment ack) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            IngestionRequestEvent event =
                    mapper.readValue(message, IngestionRequestEvent.class);
            sparkJobRunner.handle(event);
            ack.acknowledge();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
