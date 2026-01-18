package com.dataplatform.is.service;

import com.dataplatform.is.model.IngestionRequestEvent;
import com.dataplatform.is.model.JobSubmissionRequest;
import com.dataplatform.is.spark.SparkJobRunner;
import org.springframework.stereotype.Service;

@Service
public class IngestionEventHandler {


    private final PipelineRunService pipelineRunService;
    private final SparkJobRunner sparkJobRunner;

    public IngestionEventHandler(PipelineRunService pipelineRunService,
                                 SparkJobRunner sparkJobRunner) {

        this.pipelineRunService = pipelineRunService;
        this.sparkJobRunner = sparkJobRunner;
    }

    public void handle(IngestionRequestEvent event) {

        try {
            // 1. create pipeline run entry
            pipelineRunService.startRun(event);

            JobSubmissionRequest req = new JobSubmissionRequest();
            req.setJobName("spark-job-ingestion");
            req.setDataset(event.getDataset());
            req.setSourceType(event.getSourceType());
            req.setParams(event.getParams());

            // 🔥 Fire-and-forget
            sparkJobRunner.submit(req);

            // 3. update status
            pipelineRunService.markIngestedTriggered(event.getPipelineRunId());
        } catch (Exception e) {
            pipelineRunService.markFailed(event.getPipelineRunId(), e.getClass().getSimpleName(), e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
