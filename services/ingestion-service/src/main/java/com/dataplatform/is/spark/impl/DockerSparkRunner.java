package com.dataplatform.is.spark.impl;

import com.dataplatform.is.model.JobSubmissionRequest;
import com.dataplatform.is.spark.SparkJobRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile("dev")
public class DockerSparkRunner implements SparkJobRunner {

    @Override
    public void submit(JobSubmissionRequest req) {

        List<String> cmd = new ArrayList<>();

        cmd.add("docker");
        cmd.add("exec");
        cmd.add("spark-master");
        cmd.add("/opt/spark/bin/spark-submit");

        // ===============================
        // Spark master
        // ===============================
        cmd.add("--master");
        cmd.add("spark://spark-master:7077");

        // ===============================
        // Resource limits (CRITICAL)
        // ===============================
        cmd.add("--conf"); cmd.add("spark.executor.instances=1");
        cmd.add("--conf"); cmd.add("spark.executor.cores=1");
        cmd.add("--conf"); cmd.add("spark.executor.memory=512m");
        cmd.add("--conf"); cmd.add("spark.driver.memory=512m");
        cmd.add("--conf"); cmd.add("spark.cores.max=1");

        // ===============================
        // Dependency resolution
        // ===============================
        cmd.add("--conf");
        cmd.add("spark.jars.ivy=/tmp/.ivy2");

        cmd.add("--packages");
        cmd.add(
                "org.apache.hadoop:hadoop-aws:3.3.4," +
                        "org.apache.hadoop:hadoop-common:3.3.4," +
                        "com.amazonaws:aws-java-sdk-bundle:1.12.367"
        );

        // ===============================
        // S3A → LocalStack
        // ===============================
        cmd.add("--conf"); cmd.add("spark.hadoop.fs.s3a.endpoint=http://localstack:4566");
        cmd.add("--conf"); cmd.add("spark.hadoop.fs.s3a.access.key=test");
        cmd.add("--conf"); cmd.add("spark.hadoop.fs.s3a.secret.key=test");
        cmd.add("--conf"); cmd.add("spark.hadoop.fs.s3a.path.style.access=true");
        cmd.add("--conf"); cmd.add("spark.hadoop.fs.s3a.connection.ssl.enabled=false");
        cmd.add("--conf"); cmd.add("spark.hadoop.fs.s3a.impl=org.apache.hadoop.fs.s3a.S3AFileSystem");

        // ===============================
        // App name
        // ===============================
        cmd.add("--name");
        cmd.add("ingestion-" + req.getPipelineRunId());

        // ===============================
        // Job path (MUST be after options)
        // ===============================
        cmd.add(req.getJobPath());

        // ===============================
        // Job arguments
        // ===============================
        cmd.add("--pipelineRunId"); cmd.add(req.getPipelineRunId());
        cmd.add("--dataset"); cmd.add(req.getDataset());
        cmd.add("--sourceType"); cmd.add(req.getSourceType().name());

        req.getSourceConfig().forEach((k, v) -> {
            cmd.add("--" + k);
            cmd.add(v);
        });

        System.out.println("Spark submit command:");
        System.out.println(String.join(" ", cmd));

        try {
            Process process = new ProcessBuilder(cmd)
                    .redirectErrorStream(true)
                    .start();

            new Thread(() -> {
                try (var reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(process.getInputStream()))) {
                    reader.lines().forEach(System.out::println);
                } catch (Exception ignored) {}
            }).start();

        } catch (Exception e) {
            throw new RuntimeException("Failed to submit Spark job", e);
        }
    }
}
