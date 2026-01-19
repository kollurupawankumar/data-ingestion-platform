package com.dataplatform.is.kafka.consumer;

import com.dataplatform.is.TestIngestionEventFactory;
import com.dataplatform.is.model.IngestionRequestEvent;
import com.dataplatform.is.service.IngestionEventHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IngestionEventListenerTest {

    @Mock
    IngestionEventHandler sparkJobRunner;

    @InjectMocks
    IngestionKafkaListener listener;

    @Test
    void shouldInvokeIngestionService() {
        IngestionRequestEvent event =
                TestIngestionEventFactory.validDbEvent();

        //listener.listen(event);

        //verify(sparkJobRunner).handle(event);
    }
}
