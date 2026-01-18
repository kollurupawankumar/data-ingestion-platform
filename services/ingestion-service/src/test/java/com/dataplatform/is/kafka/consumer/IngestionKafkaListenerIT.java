package com.dataplatform.is.kafka.consumer;

import com.dataplatform.is.model.IngestionRequestEvent;
import com.dataplatform.is.service.IngestionEventHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest(
        classes = IngestionKafkaListener.class,
        properties = {
                "spring.kafka.consumer.group-id=test-group",
                "spring.kafka.consumer.auto-offset-reset=earliest",
                "spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer",
                "spring.kafka.consumer.properties.spring.json.trusted.packages=*",
                "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer"
        }
)
@EnableAutoConfiguration(exclude = {
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
        org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration.class
})
@ComponentScan(basePackages = "com.dataplatform.is.kafka.consumer")
@EmbeddedKafka(
        partitions = 1,
        topics = "ingestion-requests"
)
@ActiveProfiles("test")
class IngestionKafkaListenerIT {

    @Autowired
    private KafkaTemplate<String, IngestionRequestEvent> kafkaTemplate;

    // ✅ replaces Spring bean used by Kafka listener
    @MockBean
    private IngestionEventHandler ingestionEventHandler;

    @Test
    void shouldConsumeKafkaMessage() {

        IngestionRequestEvent event = new IngestionRequestEvent();
        event.setDataset("sales");
        event.setSourceType("FILE");
        event.setPipelineRunId("run-101");

        kafkaTemplate.send("ingestion-requests", event);

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        verify(ingestionEventHandler)
                                .handle(any(IngestionRequestEvent.class))
                );
    }
}
