package com.dataplatform.is.service;

import com.dataplatform.is.model.IngestionRequestEvent;
import com.dataplatform.is.model.JobSubmissionRequest;
import com.dataplatform.is.model.PipelineState;
import com.dataplatform.is.model.SourceType;
import com.dataplatform.is.spark.SparkJobRunner;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class IngestionEventHandler {


    private final PipelineRunTrackingService pipelineRunTrackingService;
    private final SparkJobRunner sparkJobRunner;

    public IngestionEventHandler(PipelineRunTrackingService pipelineRunTrackingService,
                                 SparkJobRunner sparkJobRunner) {

        this.pipelineRunTrackingService = pipelineRunTrackingService;
        this.sparkJobRunner = sparkJobRunner;
    }

    public void handle(IngestionRequestEvent event) {

        try {
            // 1. create pipeline run entry
            pipelineRunTrackingService.startRun(event);

            JobSubmissionRequest req = new JobSubmissionRequest();
            req.setJobName("spark-job-ingestion");
            req.setPipelineRunId(event.getPipelineRunId());
            req.setDataset(event.getDataset());
            req.setSourceType(event.getSourceType());
            req.setSourceConfig(event.getSourceConfig());

            req.setJobName(event.getJobName());
            req.setJobPath(event.getJobPath());

            // 🔥 Fire-and-forget
            sparkJobRunner.submit(req);

            // 3. update status
            pipelineRunTrackingService.transitionState(event.getPipelineRunId(), PipelineState.INGESTION_TRIGGERED);
        } catch (Exception e) {
            pipelineRunTrackingService.failRun(event.getPipelineRunId(), e.getClass().getSimpleName(), e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
