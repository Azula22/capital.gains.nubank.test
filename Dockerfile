FROM eclipse-temurin:19.0.1_10-jdk as builder

ENV SBT_VERSION=1.10.11 \
    SCALA_VERSION=3.3.6

RUN apt-get update && apt-get install -y curl unzip && \
    rm -rf /var/lib/apt/lists/*

RUN curl -L -o /tmp/sbt.tgz https://github.com/sbt/sbt/releases/download/v${SBT_VERSION}/sbt-${SBT_VERSION}.tgz && \
    tar -xvzf /tmp/sbt.tgz -C /opt && \
    ln -s /opt/sbt/bin/sbt /usr/local/bin/sbt

WORKDIR /app
COPY . .

RUN sbt update
RUN sbt compile
RUN sbt test

CMD ["sbt", "run"]