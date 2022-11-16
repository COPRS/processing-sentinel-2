#### Build image from pre-built image

FROM artifactory.coprs.esa-copernicus.eu/s2-docker/s2level1-jdk17:6.1.0

USER root

COPY *.jar /wrapper/app.jar
RUN chown -R wrapper:wrapper /wrapper

WORKDIR /wrapper
USER wrapper

ENTRYPOINT ["/wrapper/jdk/bin/java","-jar","/wrapper/app.jar"]
