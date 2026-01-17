# Metadata Service

## Overview

The **Metadata Service** is the core control-plane service of the data ingestion platform. It is responsible for managing all metadata required to drive ingestion, transformation, enrichment, and sink processes in a **metadata-driven, event-driven architecture**.

This service does **not** move data itself. Instead, it acts as the **single source of truth** for:

* What data to ingest
* From where to ingest
* How to transform and enrich
* Where to publish or store the data

Downstream services (ingestion, transformation, enrichment, sink) consume metadata and react to metadata changes via events.

---

## Responsibilities

The Metadata Service is responsible for:

* Managing **Subject Areas** (e.g., workforce, finance)
* Managing **Dataset definitions** (logical datasets)
* Storing **source metadata** (files, DBs, APIs, Kafka, etc.)
* Storing **sink metadata** (Hive, RDS, Postgres, etc.)
* Defining **pipelines** and execution order
* Tracking **job executions and status**
* Publishing metadata change events

---

## What This Service Is NOT

* ❌ It does not ingest data
* ❌ It does not run Spark or batch jobs
* ❌ It does not perform transformations
* ❌ It does not store business data

It only manages **metadata and orchestration signals**.

---

## Architecture Context

```
        UI (React)
            |
            v
    Metadata Service (Spring Boot)
            |
            v
      Metadata Database (Postgres)
            |
            v
       Event Bus (Kafka)
            |
            v
  Ingestion / Transform / Sink Services
```

---

## Key Concepts

### Subject Area

A **Subject Area** represents a business domain.

Example:

* workforce
* finance
* sales

---

### Dataset

A **Dataset** is a logical data entity within a subject area.

Example (workforce):

* employee
* department
* salgrade

---

### Source Metadata

Defines **where and how** the data is sourced.

Examples:

* File (CSV, JSON)
* Relational Database
* REST API
* Kafka topic

---

### Sink Metadata

Defines **where the processed data is stored**.

Examples:

* Hive table
* Postgres table
* RDS

---

### Pipeline

Defines the **end-to-end flow** for a dataset:

```
Source → Transform → Enrich → Sink
```

Each step is driven entirely by metadata.

---

### Job Execution

Tracks runtime information for a pipeline execution:

* Execution status
* Start / end time
* Errors
* Retry count

---

## Metadata-Driven Design

This platform follows a **metadata-first approach**:

* Pipelines are defined using metadata
* Services are generic and reusable
* Adding a new dataset requires **no code changes**

Only metadata changes.

---

## Technology Stack

### Backend

* Java 17+
* Spring Boot
* Spring Data JPA
* Flyway (DB migrations)
* PostgreSQL
* Apache Kafka (eventing)
* Micrometer + Prometheus

### Infrastructure

* Docker
* Docker Compose
* LocalStack (future AWS simulation)

### Observability

* Centralized logging
* Distributed tracing (planned)
* Metrics exposed via Actuator

---

## Database & Migrations

* The Metadata Service owns its schema
* All schema changes are versioned using **Flyway**
* Migration files are located at:

```
src/main/resources/db/migration/
```

---

## Configuration

Environment-specific configuration is managed via:

* `application.yaml`
* `application-dev.yaml`

---

## API Philosophy

* REST-based APIs
* CRUD operations for metadata entities
* Events emitted on metadata changes

---

## Events

The service emits events such as:

* SubjectAreaCreated
* DatasetRegistered
* PipelineUpdated
* JobTriggered

These events drive downstream processing.

---

## Future Enhancements

* UI-driven metadata management
* Versioned datasets
* Approval workflows
* Multi-tenant support
* RBAC and security

---

## Development Principles

* Metadata-first
* Event-driven
* Loosely coupled services
* Infrastructure as code
* Observability by default

---

## Status

🚧 **Active Development**

This service is the foundation of the ingestion platform and will evolve incrementally.
