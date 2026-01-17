import argparse
import json
from pyspark.sql import SparkSession
from kafka import KafkaProducer
import requests

# ---------------------------
# Spark session
# ---------------------------
spark = SparkSession.builder \
    .appName("spark-job-ingestion") \
    .getOrCreate()

# ---------------------------
# Arguments
# ---------------------------
parser = argparse.ArgumentParser()

parser.add_argument("--dataset")
parser.add_argument("--sourceType")

# FILE
parser.add_argument("--inputPath")
parser.add_argument("--format")

# DB
parser.add_argument("--dbUrl")
parser.add_argument("--dbTable")
parser.add_argument("--dbUser")
parser.add_argument("--dbPassword")

# API
parser.add_argument("--apiUrl")
parser.add_argument("--apiToken")

args = parser.parse_args()

# ---------------------------
# Ingestion logic
# ---------------------------
if args.sourceType == "FILE":
    df = spark.read.format(args.format).load(args.inputPath)

elif args.sourceType == "DB":
    df = spark.read \
        .format("jdbc") \
        .option("url", args.dbUrl) \
        .option("dbtable", args.dbTable) \
        .option("user", args.dbUser) \
        .option("password", args.dbPassword) \
        .load()

elif args.sourceType == "API":
    resp = requests.get(
        args.apiUrl,
        headers={"Authorization": f"Bearer {args.apiToken}"}
    )
    data = resp.json()
    df = spark.read.json(
        spark.sparkContext.parallelize([json.dumps(data)])
    )

else:
    raise Exception("Unsupported sourceType")

# ---------------------------
# Write Bronze (Delta)
# ---------------------------
bronze_path = f"s3a://lake/bronze/{args.dataset}"

df.write \
  .format("delta") \
  .mode("append") \
  .save(bronze_path)

# ---------------------------
# Publish JOB_COMPLETED event
# ---------------------------
producer = KafkaProducer(
    bootstrap_servers="kafka:29092",
    value_serializer=lambda v: json.dumps(v).encode("utf-8")
)

producer.send(
    "pipeline-events",
    {
        "eventType": "INGESTION_JOB_COMPLETED",
        "dataset": args.dataset,
        "bronzePath": bronze_path,
        "status": "SUCCESS"
    }
)

producer.flush()
producer.close()

spark.stop()
