FROM apache/spark:3.5.1

USER root

# -----------------------
# Install Python deps
# -----------------------
RUN pip install --no-cache-dir \
    kafka-python \
    requests

# -----------------------
# Delta Lake
# -----------------------
ENV DELTA_VERSION=3.1.0

RUN curl -L -o /opt/spark/jars/delta-core_2.12-${DELTA_VERSION}.jar \
    https://repo1.maven.org/maven2/io/delta/delta-core_2.12/${DELTA_VERSION}/delta-core_2.12-${DELTA_VERSION}.jar && \
    curl -L -o /opt/spark/jars/delta-storage-${DELTA_VERSION}.jar \
    https://repo1.maven.org/maven2/io/delta/delta-storage/${DELTA_VERSION}/delta-storage-${DELTA_VERSION}.jar

# -----------------------
# Hadoop AWS (S3A)
# -----------------------
RUN curl -L -o /opt/spark/jars/hadoop-aws-3.3.6.jar \
    https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-aws/3.3.6/hadoop-aws-3.3.6.jar && \
    curl -L -o /opt/spark/jars/aws-java-sdk-bundle-1.12.664.jar \
    https://repo1.maven.org/maven2/com/amazonaws/aws-java-sdk-bundle/1.12.664/aws-java-sdk-bundle-1.12.664.jar

USER spark
