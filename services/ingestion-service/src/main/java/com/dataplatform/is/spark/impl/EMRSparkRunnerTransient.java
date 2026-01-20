package com.dataplatform.is.spark.impl;



import com.dataplatform.is.model.JobSubmissionRequest;
import com.dataplatform.is.spark.SparkJobRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.emr.EmrClient;
import software.amazon.awssdk.services.emr.model.*;

import java.util.List;
import java.util.logging.Logger;


@Component
@Profile("prod")
public class EMRSparkRunnerTransient implements SparkJobRunner {

    private static final Logger log =
            Logger.getLogger(EMRSparkRunnerTransient.class.getName());

    private final EmrClient emrClient;

    public EMRSparkRunnerTransient(EmrClient emrClient) {
        this.emrClient = emrClient;
    }

    @Override
    public void submit(JobSubmissionRequest req) {

        // Idempotency check
        String existingClusterId = findExistingCluster(req.getPipelineRunId());
        if (existingClusterId != null) {
            log.info("EMR cluster already exists for pipelineRunId="+ req.getPipelineRunId());
            return;
        }

        // Create cluster
        String clusterId = createCluster(req);

        // 3️⃣ Submit Spark step
        submitSparkStep(clusterId, req);

        log.info("Spark job submitted on EMR cluster " +clusterId);
    }

    // ---------------------------------------------------------------------

    private String createCluster(JobSubmissionRequest req) {

        RunJobFlowRequest request = RunJobFlowRequest.builder()
                .name("ingestion-" + req.getPipelineRunId())
                .releaseLabel("emr-6.15.0")
                .applications(
                        Application.builder().name("Spark").build()
                )
                .instances(JobFlowInstancesConfig.builder()
                        .instanceGroups(
                                InstanceGroupConfig.builder()
                                        .instanceRole(InstanceRoleType.MASTER)
                                        .instanceType("m5.large")
                                        .instanceCount(1)
                                        .build(),
                                InstanceGroupConfig.builder()
                                        .instanceRole(InstanceRoleType.CORE)
                                        .instanceType("m5.large")
                                        .instanceCount(1)
                                        .build()
                        )
                        .keepJobFlowAliveWhenNoSteps(false) // 🔥 auto-terminate
                        .build()
                )
                .serviceRole("EMR_DefaultRole")
                .jobFlowRole("EMR_EC2_DefaultRole")
                .logUri("s3://your-log-bucket/emr/")
                .tags(
                        Tag.builder()
                                .key("pipelineRunId")
                                .value(req.getPipelineRunId())
                                .build()
                )
                .visibleToAllUsers(true)
                .build();

        RunJobFlowResponse response = emrClient.runJobFlow(request);
        return response.jobFlowId();
    }

    private void submitSparkStep(String clusterId, JobSubmissionRequest req) {

        HadoopJarStepConfig sparkStep = HadoopJarStepConfig.builder()
                .jar("command-runner.jar")
                .args(buildSparkArgs(req))
                .build();

        StepConfig step = StepConfig.builder()
                .name("spark-ingestion-step")
                .hadoopJarStep(sparkStep)
                .actionOnFailure(ActionOnFailure.TERMINATE_CLUSTER)
                .build();

        emrClient.addJobFlowSteps(
                AddJobFlowStepsRequest.builder()
                        .jobFlowId(clusterId)
                        .steps(step)
                        .build()
        );
    }

    private List<String> buildSparkArgs(JobSubmissionRequest req) {

        List<String> args = new java.util.ArrayList<>();

        args.add("spark-submit");
        args.add("--deploy-mode"); args.add("cluster");
        args.add("--master"); args.add("yarn");

        args.add(req.getJobPath());

        args.add("--pipelineRunId"); args.add(req.getPipelineRunId());
        args.add("--dataset"); args.add(req.getDataset());
        args.add("--sourceType"); args.add(req.getSourceType().name());

        req.getSourceConfig().forEach((k, v) -> {
            args.add("--" + k);
            args.add(v);
        });

        return args;
    }

    // ---------------------------------------------------------------------
    // Idempotency
    // ---------------------------------------------------------------------

    private String findExistingCluster(String pipelineRunId) {

        ListClustersResponse response = emrClient.listClusters(
                ListClustersRequest.builder()
                        .clusterStates(
                                ClusterState.STARTING,
                                ClusterState.BOOTSTRAPPING,
                                ClusterState.RUNNING,
                                ClusterState.WAITING
                        )
                        .build()
        );

        for (ClusterSummary cluster : response.clusters()) {
            DescribeClusterResponse describe =
                    emrClient.describeCluster(
                            DescribeClusterRequest.builder()
                                    .clusterId(cluster.id())
                                    .build()
                    );

            boolean match = describe.cluster().tags().stream()
                    .anyMatch(t ->
                            t.key().equals("pipelineRunId") &&
                                    t.value().equals(pipelineRunId)
                    );

            if (match) {
                return cluster.id();
            }
        }
        return null;
    }
}

