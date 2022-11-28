#### Build image from pre-built image

FROM s2-ipf-l0u-jdk17

USER root

COPY *.jar /wrapper/app.jar
RUN chown -R wrapper:wrapper /wrapper

WORKDIR /wrapper
USER wrapper

ENTRYPOINT ["/wrapper/jdk/bin/java","-jar","/wrapper/app.jar"]
