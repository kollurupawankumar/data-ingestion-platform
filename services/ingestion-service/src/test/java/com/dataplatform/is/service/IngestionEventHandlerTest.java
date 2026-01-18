package com.dataplatform.is.service;

import com.dataplatform.is.model.IngestionRequestEvent;
import com.dataplatform.is.model.JobSubmissionRequest;
import com.dataplatform.is.spark.SparkJobRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngestionEventHandlerTest {

    @Mock
    private PipelineRunService pipelineRunService;

    @Mock
    private SparkJobRunner sparkJobRunner;

    @InjectMocks
    private IngestionEventHandler handler;

    @Test
    void shouldStartPipelineSubmitSparkJobAndUpdateStatus() {

        // given
        IngestionRequestEvent event = new IngestionRequestEvent();
        event.setDataset("sales");
        event.setSourceType("FILE");
        event.setPipelineRunId("run-101");

        // when
        handler.handle(event);

        // then — verify order (VERY IMPORTANT)
        InOrder inOrder = inOrder(pipelineRunService, sparkJobRunner);

        inOrder.verify(pipelineRunService).startRun(event);
        inOrder.verify(sparkJobRunner).submit(any(JobSubmissionRequest.class));
        inOrder.verify(pipelineRunService)
                .markIngestedTriggered("run-101");
    }

    @Test
    void shouldBuildCorrectSparkJobRequest() {

        // given
        IngestionRequestEvent event = new IngestionRequestEvent();
        event.setDataset("customers");
        event.setSourceType("DB");

        // capture spark request
        ArgumentCaptor<JobSubmissionRequest> captor =
                ArgumentCaptor.forClass(JobSubmissionRequest.class);

        // when
        handler.handle(event);

        // then
        verify(sparkJobRunner).submit(captor.capture());

        JobSubmissionRequest req = captor.getValue();
        assertThat(req.getJobName()).isEqualTo("spark-job-ingestion");
        assertThat(req.getDataset()).isEqualTo("customers");
        assertThat(req.getSourceType()).isEqualTo("DB");
    }

    @Test
    void shouldNotMarkIngestedIfSparkSubmissionFails() {

        // given
        IngestionRequestEvent event = new IngestionRequestEvent();
        event.setPipelineRunId("run-999");

        doThrow(new RuntimeException("Spark down"))
                .when(sparkJobRunner)
                .submit(any());

        // when
        try {
            handler.handle(event);
        } catch (Exception ignored) {}

        // then
        verify(pipelineRunService).startRun(event);
        verify(pipelineRunService, never())
                .markIngestedTriggered(any());
    }

    @Test
    void shouldMarkPipelineFailedWhenSparkSubmissionFails() {

        IngestionRequestEvent event = new IngestionRequestEvent();
        event.setPipelineRunId("run-404");

        doThrow(new RuntimeException("Spark error"))
                .when(sparkJobRunner).submit(any());

        assertThrows(RuntimeException.class,
                () -> handler.handle(event));

        verify(pipelineRunService)
                .markFailed(eq("run-404"),any(), any());
    }
}

