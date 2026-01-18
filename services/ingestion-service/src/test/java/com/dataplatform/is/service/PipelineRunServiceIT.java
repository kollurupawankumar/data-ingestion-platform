package com.dataplatform.is.service;



import com.dataplatform.is.model.IngestionRequestEvent;

import com.dataplatform.is.model.PipelineEvent;
import com.dataplatform.is.spark.SparkJobRunner;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional   //AUTO ROLLBACK AFTER EACH TEST
class PipelineRunServiceIT {

    @Autowired
    private PipelineRunService pipelineRunService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private SparkJobRunner sparkJobRunner;


    Map<String, String> params = new HashMap<>();



    @Test
    void shouldCreatePipelineRunOnStart() {

        // given
        IngestionRequestEvent event = new IngestionRequestEvent();
        event.setPipelineRunId("run-101");
        event.setDataset("sales");
        event.setSourceType("FILE");
        params.put("LoadType", "FULL");
        event.setParams(params);

        // when
        pipelineRunService.startRun(event);

        // then
        PipelineEvent run = pipelineRunService.getPipelineRunById("run-101");

        assertThat(run).isNotNull();
        assertThat(run.getRunId()).isEqualTo("run-101");
        assertThat(run.getDataset()).isEqualTo("sales");
        assertThat(run.getStatus()).isEqualTo("INGESTION_STARTED");
        assertThat(run.getStartedAt()).isNotNull();
        assertThat(run.getLastUpdatedAt()).isNotNull();
    }

    @Test
    void shouldUpdateStatusToIngestedTriggered() {

        // given
        startRun("run-102");

        // when
        pipelineRunService.markIngestedTriggered("run-102");

        // then
        PipelineEvent run = pipelineRunService.getPipelineRunById("run-102");
        System.out.println(run.toString());
        assertThat(run.getStatus()).isEqualTo("INGESTION_TRIGGERED");
        assertThat(run.getLastUpdatedAt()).isNotNull();
    }

    @Test
    void shouldMarkPipelineAsFailedWithErrorDetails() {

        // given
        startRun("run-103");

        // when
        pipelineRunService.markFailed(
                "run-103",
                "SPARK_SUBMISSION_FAILED",
                "Spark cluster unreachable"
        );

        // then
        PipelineEvent run = pipelineRunService.getPipelineRunById("run-103");

        assertThat(run.getStatus()).isEqualTo("INGESTION_FAILED");
        assertThat(run.getErrorCode()).isEqualTo("SPARK_SUBMISSION_FAILED");
        assertThat(run.getErrorMessage()).contains("Spark cluster");
        assertThat(run.getEndedAt()).isNotNull();
    }

    @Test
    void shouldBeIdempotentForSameRunId() {

        // given
        IngestionRequestEvent event = new IngestionRequestEvent();
        event.setPipelineRunId("run-104");
        event.setDataset("orders");
        params.put("LoadType", "FULL");
        event.setParams(params);

        // when
        pipelineRunService.startRun(event);
        pipelineRunService.startRun(event); // duplicate call

        // then
        PipelineEvent run = pipelineRunService.getPipelineRunById("run-104");
        System.out.println(run.toString());
        assertThat(run).isNotNull();
        assertThat(run.getDataset()).isEqualTo("orders");
        assertThat(run.getStatus()).isEqualTo("INGESTION_STARTED");
    }

    // -------- helper --------
    private void startRun(String runId) {
        IngestionRequestEvent event = new IngestionRequestEvent();
        event.setPipelineRunId(runId);
        event.setDataset("test-dataset");
        params.put("LoadType", "FULL");
        event.setParams(params);
        pipelineRunService.startRun(event);
    }
}

