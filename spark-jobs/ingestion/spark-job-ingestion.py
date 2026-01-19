import argparse
import json
from pyspark.sql import SparkSession
from pyspark.sql.types import (
    StructType, StructField,
    StringType, IntegerType, LongType,
    DoubleType, BooleanType, TimestampType
)
from kafka import KafkaProducer

# =====================================================
# Argument Parsing
# =====================================================

def parse_args():
    parser = argparse.ArgumentParser()

    # Core
    parser.add_argument("--pipelineRunId", required=True)
    parser.add_argument("--dataset", required=True)
    parser.add_argument("--sourceType", required=True)  # FILE | DB | API

    # FILE
    parser.add_argument("--inputPath")
    parser.add_argument("--format")                     # csv | json | txt | parquet | avro
    parser.add_argument("--header", default="true")
    parser.add_argument("--delimiter", default=",")

    # TARGET TABLE (MANDATORY)
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


def publish_event(producer, args, status, message=None, table=None):
    event = {
        "eventType": f"INGESTION_JOB_{status}",
        "pipelineRunId": args.pipelineRunId,
        "dataset": args.dataset,
        "status": status,
        "table": table,
        "message": message
    }
    producer.send("pipeline-events", event)
    producer.flush()


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
# Bronze Delta Writer (MANDATORY TABLE)
# =====================================================

def write_bronze_table(df, spark, database, table):
    spark.sql(f"CREATE DATABASE IF NOT EXISTS {database}")

    df.write \
        .format("delta") \
        .mode("append") \
        .saveAsTable(f"{database}.{table}")

    return f"{database}.{table}"


# =====================================================
# Main Orchestration (THIN)
# =====================================================

def main():
    args = parse_args()
    spark = create_spark_session(args.pipelineRunId)
    producer = create_kafka_producer()

    schema = build_schema(args.schema)

    try:
        if args.sourceType == "FILE":
            df = read_file(spark, args, schema)

        elif args.sourceType == "DB":
            df = read_db(spark, args, schema)

        elif args.sourceType == "API":
            df = read_api(spark, args, schema)

        else:
            raise Exception(f"Unsupported sourceType: {args.sourceType}")

        full_table_name = write_bronze_table(
            df,
            spark,
            args.targetDatabase,
            args.targetTable
        )

        publish_event(
            producer,
            args,
            status="INGESTION_JOB_COMPLETED",
            table=full_table_name
        )

    except Exception as e:
        publish_event(
            producer,
            args,
            status="INGESTION_JOB_FAILED",
            message=str(e)
        )
        raise

    finally:
        producer.close()
        spark.stop()


if __name__ == "__main__":
    main()
