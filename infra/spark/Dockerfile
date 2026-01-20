FROM apache/spark:3.5.1

USER root

# --------------------------------------------------
# Hadoop AWS – MUST MATCH Spark's Hadoop (3.3.6)
# --------------------------------------------------
ADD https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-aws/3.3.6/hadoop-aws-3.3.6.jar /opt/spark/jars/
ADD https://repo1.maven.org/maven2/com/amazonaws/aws-java-sdk-bundle/1.12.540/aws-java-sdk-bundle-1.12.540.jar /opt/spark/jars/

# --------------------------------------------------
# Python dependency
# --------------------------------------------------
RUN pip install --no-cache-dir kafka-python

# --------------------------------------------------
# REMOVE Delta configs completely
# --------------------------------------------------
RUN sed -i '/delta/d' /opt/spark/conf/spark-defaults.conf || true

USER spark
