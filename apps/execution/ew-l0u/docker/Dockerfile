#### Setup

ARG INV_INSTALL="/usr/local/components/facilities/DPC-CORE-l0pack-ing-typ/DPC-CORE-l0pack-ing-typ-3.0.3"
ARG PROC_STATION=REFS

ARG JDK_URI=https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_linux-x64_bin.tar.gz
ARG JDK_VERSION=17.0.2

#### Fetch JDK

FROM ubuntu:focal AS jdk

RUN apt-get update \
    && apt-get install --no-install-recommends -y ca-certificates curl \
    && rm -rf /var/lib/apt/lists/*

ARG JDK_URI
RUN curl -L $JDK_URI > jdk.tar.gz

#### Build image from IPF

FROM artifactory.coprs.esa-copernicus.eu/s2-docker/s2-ipf-l0u:3.0.1

USER root

## Wrapper configuration
ARG JDK_VERSION
ARG INV_INSTALL
RUN adduser -p '*' -d /wrapper wrapper
RUN usermod -a -G wrapper ops2pdgs
RUN chmod -R 777 $INV_INSTALL  ## Barbaric but quick fix
RUN chmod 755 /wrapper
COPY --from=jdk jdk.tar.gz /wrapper/jdk.tar.gz
RUN cd /wrapper \
    && tar xvf jdk.tar.gz \
    && mv jdk-$JDK_VERSION jdk \
    && rm jdk.tar.gz
COPY *.jar /wrapper/app.jar
RUN chown -R wrapper:wrapper /wrapper

# Set config values
ARG INV_INSTALL
ARG PROC_STATION
RUN sed -i 's+<center>.*</center>+<center>'"$PROC_STATION"'</center>+' ${INV_INSTALL}/*/conf/standalone/DPC_config_center.xml
RUN sed -i 's+<archiving_center>.*</archiving_center>+<archiving_center>'"$PROC_STATION"'</archiving_center>+' ${INV_INSTALL}/*/conf/standalone/DPC_config_center.xml

## Setup workspace folder
RUN mkdir -p /workspace
RUN chmod -R 755 /workspace
RUN chown -R wrapper:wrapper /workspace

## Setup OUTPUT folder (on shared volume)
RUN mkdir -p /output
RUN chmod -R 755 /output
RUN chown -R wrapper:wrapper /output

WORKDIR /wrapper
USER wrapper

ENTRYPOINT ["/wrapper/jdk/bin/java","-jar","/wrapper/app.jar"]
