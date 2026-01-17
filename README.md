# Data Ingestion Platform (Metadata‑Driven, Event‑Driven)

## Overview

This project is a **metadata‑driven data ingestion platform** built using a modern **Java microservices architecture**. The platform ingests data from multiple sources (files, databases, APIs, Kafka), processes it through configurable stages (ingestion → transformation → enrichment), and loads it into configurable sinks (PostgreSQL / RDS / Hive later).

The core philosophy of the platform is:

> **Change behavior through metadata, not code.**

The entire system is designed to run **locally using Docker**, with AWS‑like components simulated using **LocalStack**, making it ideal for development, experimentation, and learning cloud‑native data platform design.

---

## Key Design Principles

* **Metadata‑Driven** – Pipelines are defined via YAML + database metadata, not hard‑coded logic
* **Event‑Driven** – Services communicate asynchronously using Kafka
* **Microservices** – Each stage of the pipeline is an independent Spring Boot service
* **Scalable by Design** – Stateless services, horizontal scaling, partitioned event streams
* **Observable by Default** – Metrics, logs, and traces are first‑class citizens
* **Local‑First** – Entire platform runs locally using Docker & LocalStack

---

## High‑Level Architecture

```
React UI
   │
   ▼
API Gateway
   │
   ▼
Metadata Service  ───► PostgreSQL (Metadata DB)
   │
   ▼ (events)
Orchestrator Service
   │
   ▼ (Kafka topics)
Ingestion Service
   │
   ▼
Transformation Service
   │
   ▼
Enrichment Service
   │
   ▼
Sink Service ───► PostgreSQL / RDS / Hive (future)
```

---

## Metadata‑Driven Model

The system behavior is fully controlled by **metadata**, stored using a **hybrid approach**:

### 1. YAML (Design‑Time Metadata)

* Stored in Git
* Human‑readable
* Defines intent (what should exist)

Example:

* Subject Area: `workforce`
* Entities: `employee`, `department`, `salgrade`

### 2. Database (Runtime Metadata)

* Loaded from YAML by Metadata Service
* Used by all runtime services
* Editable via UI
* Supports activation, pause, retry, overrides

> **All runtime services read metadata only from the database.**

---

## Example Subject Area

```
configs/
└── workforce/
    ├── employee.yaml
    ├── department.yaml
    └── salgrade.yaml
```

Each YAML defines:

* Source type & configuration
* Transform rules
* Enrichment rules
* Sink configuration

No code changes are required to onboard a new entity.

---

## Services (Incrementally Added)

### 1. Metadata Service (First Service)

**Responsibility:**

* Load YAML metadata into database
* Validate configurations
* Serve metadata to other services
* Act as the system’s control plane

### 2. Orchestrator Service

**Responsibility:**

* Identify active entities
* Trigger ingestion workflows
* Publish pipeline events

### 3. Ingestion Service

**Responsibility:**

* Read data from source systems
* Perform raw ingestion
* Publish downstream events

### 4. Transformation Service

**Responsibility:**

* Apply metadata‑driven transformations
* Column mapping, cleansing, normalization

### 5. Enrichment Service

**Responsibility:**

* Add derived fields
* External lookups
* Business logic enrichment

### 6. Sink Service

**Responsibility:**

* Load data into configured targets
* Handle upserts / append strategies

---

## Technology Stack

### Backend

* Java 17
* Spring Boot 3
* Spring Cloud
* Spring Kafka
* Spring Data JPA

### Frontend

* React
* Vite

### Data & Messaging

* PostgreSQL
* Apache Kafka

### Observability

* Micrometer
* OpenTelemetry
* Prometheus (metrics)
* Grafana (dashboards & alerts)
* Loki (logs)
* Tempo (distributed tracing)

### Infrastructure

* Docker
* Docker Compose
* LocalStack (AWS simulation – optional)

---

## Observability (First‑Class Feature)

The platform provides:

* Pipeline‑level metrics (success, failure, latency)
* Service‑level JVM and container metrics
* End‑to‑end distributed tracing per ingestion job
* Centralized structured logging

Grafana is used as the **single pane of glass** for monitoring and debugging.

---

## Repository Structure

```
data‑ingestion‑platform/
├── services/
│   ├── metadata‑service
│   ├── orchestrator‑service
│   ├── ingestion‑service
│   ├── transformation‑service
│   ├── enrichment‑service
│   └── sink‑service
├── ui/
│   └── admin‑ui
├── configs/
│   └── workforce/
├── infra/
│   ├── docker‑compose.yml
│   ├── prometheus/
│   └── grafana/
└── README.md
```

---

## MVP Scope

✔ One subject area (`workforce`)
✔ File‑based ingestion
✔ PostgreSQL sink
✔ Kafka‑based event flow
✔ Metadata‑driven processing
✔ Observability via Grafana

---

## Future Enhancements

* Spark‑based processing
* Hive / Data Lake sinks
* Schema evolution
* Security & RBAC
* Multi‑tenant support
* API ingestion
* Cloud deployment (EKS / ECS)

---

## Goal of the Project

This project is intended to:

* Demonstrate **real‑world data platform architecture**
* Serve as a **portfolio‑grade system design**
* Act as a foundation for a production‑ready ingestion product

---

## Status

🚧 Under active development — services will be added incrementally.
