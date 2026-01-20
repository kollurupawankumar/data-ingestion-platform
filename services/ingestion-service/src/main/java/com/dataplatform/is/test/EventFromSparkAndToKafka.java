package com.dataplatform.is.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class EventFromSparkAndToKafka {

    public static String kafkaBootstrap = "localhost:9092";
    public static String ingestionTopic = "ingestion-requests";
    public static String consumptionTopic = "pipeline-events";


    public static void mockPublishEventFromSparkJob(String runId) throws JsonProcessingException {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrap);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "INGESTION_JOB_FAILED");
        event.put("pipelineRunId", runId);
        event.put("dataset", "users");
        event.put("status", "FAILED");
        event.put("rawLocation", "s3a://bronze-bucket/workforce/users");
        event.put("silverLocation", null);
        event.put("goldLocation", null);
        event.put("errorCode", "PATH_NOT_FOUND");
        event.put("errorMessage", "[PATH_NOT_FOUND] Path does not exist: s3a://bronze-bucket/workforce/users.csv.");
        String now = LocalDateTime.now().toString();

        event.put("startedAt", now);
        event.put("endedAt", now);
        event.put("lastUpdatedAt", now);



        ObjectMapper mapper = new ObjectMapper();
        String message = mapper.writeValueAsString(event);

        producer.send(new ProducerRecord<>(consumptionTopic, runId, message));
        producer.flush();
        producer.close();
        System.out.println(message);
        System.out.println("Consumption event published for runId=" + runId);
    }

    public static void mockPublishEvenToIngestion() throws JsonProcessingException {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrap);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        String pipelineRunId = UUID.randomUUID().toString();
        System.out.println("The Run_Id is : "+pipelineRunId);

        String message = getJsonMessageForIngestionEvent(pipelineRunId);

        producer.send(new ProducerRecord<>(ingestionTopic, pipelineRunId, message));
        producer.flush();
        producer.close();
        System.out.println(message);
        System.out.println("Ingestion event published for runId=" + pipelineRunId);
    }

    private static String getJsonMessageForIngestionEvent(String pipelineRunId) throws JsonProcessingException {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "INGESTION_REQUESTED");
        event.put("pipelineRunId", pipelineRunId);
        event.put("dataset", "users");
        event.put("sourceType", "FILE");
        event.put("jobName", "Main-file-trigger-job");
        event.put("jobPath","/jobs/ingestion/spark-job-ingestion.py");


        Map<String, String> sourceConfig = new HashMap<>();
        sourceConfig.put("inputPath", "s3a://raw-bucket/workforce/users.csv");
        sourceConfig.put("format", "csv");
        sourceConfig.put("header", "true");
        sourceConfig.put("targetDatabase", "bronze");
        sourceConfig.put("targetTable", "workforce_users");
        sourceConfig.put("loadType", "workforce_users");


        event.put("sourceConfig", sourceConfig);


        ObjectMapper mapper = new ObjectMapper();
        String message = mapper.writeValueAsString(event);
        return message;
    }

    public static  S3Client createS3Client(){
        return S3Client.builder()
                .endpointOverride(URI.create("http://localhost:4566"))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("test", "test")
                        )
                )
                .region(Region.US_EAST_1)
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)   // 🔥 THIS IS THE KEY
                                .build()
                )
                .build();
    }

    public static void createBucketIfNotExists(S3Client s3Client, String bucketName) {
        try {
            s3Client.headBucket(
                    HeadBucketRequest.builder()
                            .bucket(bucketName)
                            .build()
            );
            System.out.println("Bucket already exists: " + bucketName);

        } catch (S3Exception e) {
            System.out.println("Bucket not found. Creating bucket: " + bucketName);

            s3Client.createBucket(
                    CreateBucketRequest.builder()
                            .bucket(bucketName)
                            .build()
            );
        }
    }


    public static void placeAnObjectToS3(String localFilePath, String bucket, String s3Key){
        try (S3Client s3Client = createS3Client()) {
            createBucketIfNotExists(s3Client, bucket);
            PutObjectRequest putReq = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .build();
            s3Client.putObject(putReq, new File(localFilePath).toPath());
            String s3Path = "s3a://" + bucket + "/" + s3Key;
            System.out.println("Uploaded to " + s3Path);
        }

    }




    public static void main(String[] args) throws JsonProcessingException {

        String localFilePath = "/Users/pawan/developer/data-ingestion-platform/data/users.csv";
        String bucket = "raw-bucket";
        String s3Key = "workforce/users.csv";
        String runId = "3fbdca1f-f6d0-4116-b721-882cdd2889af";

        //mockPublishEventFromSparkJob(runId);
        mockPublishEvenToIngestion();
        //placeAnObjectToS3(localFilePath, bucket, s3Key);


    }
}
