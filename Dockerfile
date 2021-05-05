FROM ubuntu:18.04

RUN apt update && apt install -y default-jre

ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64

WORKDIR /home

RUN apt install -y wget
RUN wget https://ftp.wayne.edu/apache/spark/spark-3.1.1/spark-3.1.1-bin-hadoop2.7.tgz
RUN tar -xzf spark-3.1.1-bin-hadoop2.7.tgz && rm spark-3.1.1-bin-hadoop2.7.tgz
RUN echo "export PATH=$PATH:/home/spark-3.1.1-bin-hadoop2.7/bin" >> ~/.bashrc

ENV SPARK_HOME /home/spark-3.1.1-bin-hadoop2.7

COPY target/master-1.0.jar/ ./
COPY data/model/ model/
COPY data/TestDataset.csv TestDataset.csv

ENTRYPOINT [ "sh", "-c", "/home/spark-3.1.1-bin-hadoop2.7/bin/spark-submit --class edu.njit.cloudComputing.project2.Predict master-1.0.jar" ]
