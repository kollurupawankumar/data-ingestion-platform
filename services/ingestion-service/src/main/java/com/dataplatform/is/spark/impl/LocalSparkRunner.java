package com.dataplatform.is.spark.impl;

import com.dataplatform.is.model.JobSubmissionRequest;
import com.dataplatform.is.spark.SparkJobRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@Profile("dev")
public class LocalSparkRunner implements SparkJobRunner {

    @Override
    public void submit(JobSubmissionRequest req) {

        List<String> cmd = new ArrayList<>();

        cmd.add("spark-submit");
        cmd.add("--master");
        cmd.add("local[*]");

        cmd.add("--name");
        cmd.add("ingestion-" + req.getPipelineRunId());

        cmd.add(req.getJobPath());

        cmd.add("--pipelineRunId");
        cmd.add(req.getPipelineRunId());

        cmd.add("--dataset");
        cmd.add(req.getDataset());

        cmd.add("--sourceType");
        cmd.add(req.getSourceType().name());

        req.getSourceConfig().forEach((k, v) -> {
            cmd.add("--" + k);
            cmd.add(v);
        });

        try {
            new ProcessBuilder(cmd)
                    .redirectErrorStream(true)
                    .start(); // 🔥 fire-and-forget

        } catch (Exception e) {
            // Only startup failures handled here
            //publishPipelineFailed(req, "Spark submit failed: " + e.getMessage());
            throw new RuntimeException("Failed to submit Spark job", e);
        }
    }
}
