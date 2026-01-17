# Ingestion Service
**Metadata-Driven Ingestion Platform**

---

## 1. Purpose

The **Ingestion Service** is the execution layer of the metadata-driven ingestion platform.  
It is responsible for reading data from various source systems and persisting it into the raw (bronze) zone based entirely on metadata definitions.

This service is:
- Metadata-driven
- Stateless
- Connector-based
- Horizontally scalable
- Event-driven

---

## 2. Key Responsibilities

The Ingestion Service performs the following functions:

- Executes ingestion jobs triggered by the Orchestration Service
- Fetches dataset, source, and schema metadata
- Dynamically selects the appropriate connector
- Supports **FULL** and **INCREMENTAL** loads
- Manages checkpoints for incremental ingestion
- Writes data to the raw/bronze zone
- Publishes job execution status and metrics

---

## 3. Inputs and Outputs

### 3.1 Ingestion Request (Input)

```json
{
  "pipelineRunId": "run-123",
  "datasetId": 42,
  "loadType": "INCREMENTAL",
  "triggeredBy": "SCHEDULER"
}
```
### 3.2 Execution Status Event (Output)
```json

{
  "pipelineRunId": "run-123",
  "datasetId": 42,
  "status": "SUCCESS",
  "recordsIngested": 120000,
  "durationMs": 45000,
  "timestamp": "2026-01-15T10:30:00Z"
}
```
---
## 4. High-Level Workflow
- Receive ingestion request from the Orchestration Service 
- Fetch dataset and source metadata
- Enrich request into an internal Ingestion Job 
- Resolve the correct connector using metadata
- Execute ingestion
- Persist data to the raw zone
- Update checkpoint (if incremental)
- Publish job status and metrics

## 5. Core Components
### 5.1 Ingestion Executor
Coordinates the complete execution lifecycle of an ingestion job.

### 5.2 Metadata Client
Fetches dataset, source, schema, and configuration metadata from the Metadata Service.

### 5.3 Connector Factory
Resolves the appropriate connector implementation based on the source type.

### 5.4 Connectors
Pluggable source handlers for:

- FILE

- JDBC / DATABASE

- API

- KAFKA / STREAMING

### 5.5 Checkpoint Manager
Maintains state for incremental ingestion.

### 5.6 Status Publisher
Publishes execution results and metrics to Kafka or any event bus.

## 6. Connector Model
### 6.1 Connector Interface
java
Copy code
public interface IngestionConnector {
    void ingest(IngestionJob job);
}
### 6.2 Supported Connectors

- FileConnector	FILE -	CSV, JSON, Parquet
- JdbcConnector	DB	- MySQL, Postgres, Oracle
- ApiConnector	API	- REST endpoints
- KafkaConnector	STREAM -	Kafka topics

## 7. Checkpointing Strategy
Checkpointing enables reliable incremental ingestion.

- Database -	Max updated timestamp / primary key
- File -	Last processed file name or timestamp
- API -	Last successful sync timestamp
- Kafka -	Last committed offset

## 8. Error Handling and Resilience
The service follows fault isolation and bulkhead patterns.

## Strategies
- Separate thread pools per connector type

- Retry with exponential backoff for transient failures

- Circuit breakers for unstable sources

- Timeouts for slow APIs

- Graceful failure handling with status reporting

- Technologies
- Resilience4j

- Spring Retry

## 9. Observability
### 9.1 Logging
Structured logging (JSON format)

Correlation ID: pipelineRunId

### 9.2 Metrics
Records ingested

Job duration

Failure rate

Throughput

### 9.3 Tracing
Distributed tracing using OpenTelemetry

## 10. Scalability and Performance
Stateless service design

Horizontal scaling supported

Parallel ingestion across datasets

Thread pool isolation per connector

Supports deployment on:

Kubernetes

ECS

VM-based environments

## 11. Security
Metadata access secured using OAuth2 / JWT Secrets managed using:

- Vault

- AWS Secrets Manager

- Azure Key Vault

No credentials stored in code or plain configuration files

## 12. Technology Stack
- Layer	Technology
- Framework	Spring Boot
- Messaging	Kafka
- Resilience	Resilience4j
- Metrics	Micrometer + Prometheus
- Tracing	OpenTelemetry
- Storage	S3 / ADLS / HDFS

## 13. Design Principles
- Single Responsibility – ingestion only, no orchestration logic

- Open/Closed Principle – add new connectors without changing core logic

- Loose Coupling – metadata-driven execution

- Fail Fast – validate metadata before execution

- Observable by Default – metrics and tracing built in

## 14. Operational Flow (Example)
- Orchestrator triggers ingestion for dataset 42

- Ingestion Service fetches metadata

- JdbcConnector is selected

- Incremental query is generated using checkpoint

- Data is written to the raw zone

- Checkpoint is updated

- SUCCESS event is published






