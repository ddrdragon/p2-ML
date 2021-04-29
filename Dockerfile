FROM openjdk:8-jdk-alpine

RUN apk update && \
	apk add --no-cache libc6-compat ca-certificates && \
	ln -s /lib/libc.musl-x86_64.so.1 /lib/ld-linux-x86-64.so.2 && \
	rm -rf /var/cache/apk/*

RUN set -ex
RUN mkdir -p datset

COPY out/artifacts/master_jar/ out/
COPY data/model/ /model/
#COPY data/ValidationDataset.csv /ValidationDataset.csv

VOLUME "$PWD/":/

ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar out/master.jar" ]
