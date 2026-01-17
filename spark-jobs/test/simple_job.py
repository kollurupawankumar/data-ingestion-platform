from pyspark.sql import SparkSession

spark = SparkSession.builder.appName("simple-test").getOrCreate()

data = [("pawan", 1), ("kafka", 2)]
df = spark.createDataFrame(data, ["name", "id"])

df.show()

spark.stop()
