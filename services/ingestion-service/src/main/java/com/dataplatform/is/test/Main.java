package com.dataplatform.is.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class Main {

    private static void createBucketIfNotExists(S3Client s3Client, String bucketName) {
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

    public static void main(String[] args) throws Exception {

        // -----------------------------
        // CONFIG
        // -----------------------------
        String localFilePath = "/Users/pawan/developer/data-ingestion-platform/data/users.csv";
        String bucket = "raw-bucket";
        String s3Key = "workforce/users.csv";

        String kafkaBootstrap = "localhost:9092";
        String ingestionTopic = "ingestion-requests";

        // -----------------------------
        // 1. Upload file to S3
        // -----------------------------
        /*S3Client s3Client = S3Client.builder()
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
        createBucketIfNotExists(s3Client, "raw-bucket");
        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(s3Key)
                .build();

        s3Client.putObject(putReq, new File(localFilePath).toPath());*/

        String s3Path = "s3a://" + bucket + "/" + s3Key;
        System.out.println("Uploaded to " + s3Path);

        // -----------------------------
        // 2. Publish Kafka event
        // -----------------------------
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrap);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        String pipelineRunId = UUID.randomUUID().toString();

        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "INGESTION_REQUESTED");
        event.put("pipelineRunId", pipelineRunId);
        event.put("dataset", "users");
        event.put("sourceType", "FILE");

        Map<String, String> sourceConfig = new HashMap<>();
        sourceConfig.put("inputPath", s3Path);
        sourceConfig.put("format", "csv");
        sourceConfig.put("header", "true");
        sourceConfig.put("targetDatabase", "bronze");
        sourceConfig.put("targetTable", "workforce_users");
        sourceConfig.put("LoadType", "workforce_users");




        event.put("sourceConfig", sourceConfig);


        ObjectMapper mapper = new ObjectMapper();
        String message = mapper.writeValueAsString(event);

        producer.send(new ProducerRecord<>(ingestionTopic, pipelineRunId, message));
        producer.flush();
        producer.close();
        System.out.println(message);
        System.out.println("Ingestion event published for runId=" + pipelineRunId);
    }
}

