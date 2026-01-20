import argparse
import json
from datetime import datetime

from pyspark.sql import SparkSession
from pyspark.sql.types import (
    StructType, StructField,
    StringType, IntegerType, LongType,
    DoubleType, BooleanType, TimestampType
)

from kafka import KafkaProducer

# =====================================================
# Utils
# =====================================================

def now_utc():
    return datetime.utcnow().isoformat()


# =====================================================
# Argument Parsing
# =====================================================

def parse_args():
    parser = argparse.ArgumentParser()

    # Core
    parser.add_argument("--pipelineRunId", required=True)
    parser.add_argument("--dataset", required=True)
    parser.add_argument("--name", required=False)
    parser.add_argument("--loadType", required=True)          # FULL | INCREMENTAL
    parser.add_argument("--sourceType", required=True)        # FILE | DB | API

    # FILE
    parser.add_argument("--inputPath")
    parser.add_argument("--format")                           # csv | json | txt | parquet | avro
    parser.add_argument("--header", default="true")
    parser.add_argument("--delimiter", default=",")

    # TARGET (MANDATORY)
    parser.add_argument("--targetDatabase", required=True)
    parser.add_argument("--targetTable", required=True)

    # SCHEMA (OPTIONAL – JSON STRING)
    parser.add_argument("--schema")

    # DB (future)
    parser.add_argument("--dbUrl")
    parser.add_argument("--dbTable")
    parser.add_argument("--dbUser")
    parser.add_argument("--dbPassword")

    # API (future)
    parser.add_argument("--apiUrl")
    parser.add_argument("--apiToken")

    return parser.parse_args()


# =====================================================
# Spark Session
# =====================================================

def create_spark_session(pipeline_run_id):
    return (
        SparkSession.builder
        .appName(f"spark-ingestion-{pipeline_run_id}")
        .getOrCreate()
    )


# =====================================================
# Kafka Producer
# =====================================================

def create_kafka_producer():
    return KafkaProducer(
        bootstrap_servers="kafka:29092",
        value_serializer=lambda v: json.dumps(v).encode("utf-8")
    )


def publish_pipeline_event(producer, event):
    producer.send("pipeline-events", event)
    producer.flush()


# =====================================================
# Pipeline Event Builder
# =====================================================

def build_pipeline_event(
    *,
    run_id,
    event_type,
    dataset,
    load_type,
    status,
    raw_location,
    silver_location=None,
    gold_location=None,
    error_code=None,
    error_message=None,
    started_at,
    ended_at
):
    return {
        "pipelineRunId": run_id,
        "eventType": event_type,
        "dataset": dataset,
        "loadType": load_type,
        "status": status,
        "rawLocation": raw_location,
        "silverLocation": silver_location,
        "goldLocation": gold_location,
        "errorCode": error_code,
        "errorMessage": error_message,
        "startedAt": started_at,
        "endedAt": ended_at,
        "lastUpdatedAt": ended_at
    }


# =====================================================
# Error Classification
# =====================================================

def classify_error(exception: Exception):
    msg = str(exception)

    if "PATH_NOT_FOUND" in msg or "Path does not exist" in msg:
        return "PATH_NOT_FOUND", msg
    if "AccessDenied" in msg or "Permission denied" in msg:
        return "ACCESS_DENIED", msg
    if "Unsupported file format" in msg:
        return "INVALID_FORMAT", msg

    return "UNKNOWN_ERROR", msg


# =====================================================
# Schema Handling (OPTIONAL)
# =====================================================

def build_schema(schema_json):
    if not schema_json:
        return None

    type_mapping = {
        "string": StringType(),
        "int": IntegerType(),
        "long": LongType(),
        "double": DoubleType(),
        "boolean": BooleanType(),
        "timestamp": TimestampType()
    }

    schema_def = json.loads(schema_json)

    fields = [
        StructField(col["name"], type_mapping[col["type"]], True)
        for col in schema_def
    ]

    return StructType(fields)


# =====================================================
# FILE Readers
# =====================================================

def read_csv(spark, args, schema):
    reader = (
        spark.read
        .option("header", args.header)
        .option("delimiter", args.delimiter)
    )
    if schema:
        reader = reader.schema(schema)
    return reader.csv(args.inputPath)


def read_json(spark, args, schema):
    reader = spark.read
    if schema:
        reader = reader.schema(schema)
    return reader.json(args.inputPath)


def read_txt(spark, args, schema):
    reader = spark.read
    if schema:
        reader = reader.schema(schema)
    return reader.text(args.inputPath)


def read_parquet(spark, args, schema):
    return spark.read.parquet(args.inputPath)


def read_avro(spark, args, schema):
    return spark.read.format("avro").load(args.inputPath)


def read_file(spark, args, schema):
    fmt = args.format.lower()

    readers = {
        "csv": read_csv,
        "json": read_json,
        "txt": read_txt,
        "parquet": read_parquet,
        "avro": read_avro
    }

    if fmt not in readers:
        raise Exception(f"Unsupported file format: {fmt}")

    return readers[fmt](spark, args, schema)


# =====================================================
# DB / API Readers (Extension Points)
# =====================================================

def read_db(spark, args, schema):
    reader = (
        spark.read
        .format("jdbc")
        .option("url", args.dbUrl)
        .option("dbtable", args.dbTable)
        .option("user", args.dbUser)
        .option("password", args.dbPassword)
    )
    if schema:
        reader = reader.schema(schema)
    return reader.load()


def read_api(spark, args, schema):
    raise NotImplementedError("API ingestion not implemented yet")


# =====================================================
# Bronze Writer (Parquet, LocalStack-safe)
# =====================================================

def write_bronze_table(df, database, table):
    output_path = f"s3a://bronze-bucket/{database}/{table}/"

    (
        df.write
        .mode("overwrite")
        .parquet(output_path)
    )

    return output_path


# =====================================================
# Main Orchestration
# =====================================================

def main():
    args = parse_args()
    spark = create_spark_session(args.pipelineRunId)
    producer = create_kafka_producer()

    started_at = now_utc()
    raw_location = args.inputPath
    schema = build_schema(args.schema)

    try:
        # -------------------------
        # READ
        # -------------------------

        running_event = build_pipeline_event(
                                run_id=args.pipelineRunId,
                                event_type="INGESTION_JOB_RUNNING",
                                dataset=args.dataset,
                                load_type=args.loadType,
                                status="RUNNING",
                                raw_location=raw_location,
                                silver_location=None,
                                gold_location=None,
                                started_at=started_at,
                                ended_at=now_utc()
                            )
        publish_pipeline_event(producer, running_event)

        if args.sourceType == "FILE":
            df = read_file(spark, args, schema)
        elif args.sourceType == "DB":
            df = read_db(spark, args, schema)
        elif args.sourceType == "API":
            df = read_api(spark, args, schema)
        else:
            raise Exception(f"Unsupported sourceType: {args.sourceType}")

        # -------------------------
        # WRITE BRONZE
        # -------------------------
        silver_location = write_bronze_table(
            df,
            args.targetDatabase,
            args.targetTable
        )

        ended_at = now_utc()

        success_event = build_pipeline_event(
            run_id=args.pipelineRunId,
            event_type="INGESTION_JOB_COMPLETED",
            dataset=args.dataset,
            load_type=args.loadType,
            status="SUCCESS",
            raw_location=raw_location,
            silver_location=silver_location,
            gold_location=None,
            started_at=started_at,
            ended_at=ended_at
        )

        publish_pipeline_event(producer, success_event)

    except Exception as e:
        ended_at = now_utc()
        error_code, error_message = classify_error(e)

        failure_event = build_pipeline_event(
            run_id=args.pipelineRunId,
            event_type="INGESTION_JOB_FAILED",
            dataset=args.dataset,
            load_type=args.loadType,
            status="FAILED",
            raw_location=raw_location,
            silver_location=None,
            gold_location=None,
            error_code=error_code,
            error_message=error_message,
            started_at=started_at,
            ended_at=ended_at
        )

        publish_pipeline_event(producer, failure_event)
        raise

    finally:
        producer.close()
        spark.stop()


if __name__ == "__main__":
    main()
