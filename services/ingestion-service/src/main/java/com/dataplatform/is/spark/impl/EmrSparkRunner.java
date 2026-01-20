package com.dataplatform.is.spark.impl;

import com.dataplatform.is.model.JobSubmissionRequest;
import com.dataplatform.is.spark.SparkJobRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.emr.EmrClient;
import software.amazon.awssdk.services.emr.model.AddJobFlowStepsRequest;
import software.amazon.awssdk.services.emr.model.HadoopJarStepConfig;
import software.amazon.awssdk.services.emr.model.StepConfig;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile("prod")
public class EmrSparkRunner implements SparkJobRunner {

    @Autowired
    private EmrClient emrClient;


    @Override
    public void submit(JobSubmissionRequest req) {

        // 1. Build spark-submit args
        List<String> args = new ArrayList<>();

        args.add("spark-submit");
        args.add("--deploy-mode");
        args.add("cluster");

        args.add("--class");
        args.add("org.apache.spark.deploy.PythonRunner");

        args.add(req.getJobPath());

        // params...

        // 2. Submit via EMR Steps API
        AddJobFlowStepsRequest request =
                AddJobFlowStepsRequest.builder()
                        .jobFlowId("<cluster-id>")
                        .steps(
                                StepConfig.builder()
                                        .name("ingestion-" + req.getPipelineRunId())
                                        .hadoopJarStep(
                                                HadoopJarStepConfig.builder()
                                                        .jar("command-runner.jar")
                                                        .args(args)
                                                        .build()
                                        )
                                        .actionOnFailure("CONTINUE")
                                        .build()
                        )
                        .build();

        emrClient.addJobFlowSteps(request);
    }
}

