package com.dataplatform.is.kafka.consumer;

import com.dataplatform.is.service.IngestionEventHandler;
import com.dataplatform.is.model.IngestionRequestEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class IngestionKafkaListener {

    private final IngestionEventHandler sparkJobRunner;

    public IngestionKafkaListener(IngestionEventHandler sparkJobRunner) {
        this.sparkJobRunner = sparkJobRunner;
    }

    @KafkaListener(topics = "ingestion-requests" ,
                   groupId = "ingestion-service")
    public void listen(IngestionRequestEvent event) {
        sparkJobRunner.handle(event);
    }
}
