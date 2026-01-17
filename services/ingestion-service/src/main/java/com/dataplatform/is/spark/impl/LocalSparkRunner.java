package com.dataplatform.is.spark.impl;

import com.dataplatform.is.model.JobSubmissionRequest;
import com.dataplatform.is.spark.SparkJobRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile("dev")
public class LocalSparkRunner implements SparkJobRunner {

    @Override
    @Async
    public void submit(JobSubmissionRequest req) {

        try {
            List<String> cmd = new ArrayList<>();

            cmd.add("spark-submit");
            cmd.add("--master");
            cmd.add("spark://spark-master:7077");

            cmd.add("/jobs/spark-job-ingestion.py");

            cmd.add("--dataset");
            cmd.add(req.getDataset());

            cmd.add("--sourceType");
            cmd.add(req.getSourceType());


            req.getParams().forEach((k, v) -> {
                cmd.add("--" + k);
                cmd.add(v);
            });

            new ProcessBuilder(cmd).start(); // 🔥 non-blocking

        } catch (Exception e) {
            throw new RuntimeException("Failed to submit Spark job", e);
        }
    }
}

