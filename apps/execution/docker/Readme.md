
## Note

Dockerfiles are meant to be run from the root of the project, since it copies files from the 'input' directory

## Dependencies

- S2IPF_Orchestrator and S2IPF_Orchestrator_Launcher, with their dependencies (see [here](../../../inputs/orchestrator/Readme.md))
- L2 scripts(see [here](../../../inputs/l2))

## L0u execution worker base image

```
docker build -t s2-ipf-l0u-jdk17 -f apps/execution/docker/Dockerfile_s2-ipf-l0u-jdk17 .
docker tag s2-ipf-l0u-jdk17 artifactory.coprs.esa-copernicus.eu/s2-docker/s2-ipf-l0u-jdk17:3.0.1
docker push artifactory.coprs.esa-copernicus.eu/s2-docker/s2-ipf-l0u-jdk17:3.0.1 
```

## L0c execution worker base image

```
docker build -t s2level0-jdk17 -f apps/execution/docker/Dockerfile_s2level0-jdk17 .
docker tag s2level0-jdk17 artifactory.coprs.esa-copernicus.eu/s2-docker/s2level0-jdk17:6.1.0
docker push artifactory.coprs.esa-copernicus.eu/s2-docker/s2level0-jdk17:6.1.0 
```

## L1 execution workers base image

```
docker build -t s2level1-jdk17 -f apps/execution/docker/Dockerfile_s2level1-jdk17 .
docker tag s2level1-jdk17 artifactory.coprs.esa-copernicus.eu/s2-docker/s2level1-jdk17:6.1.0
docker push artifactory.coprs.esa-copernicus.eu/s2-docker/s2level1-jdk17:6.1.0
```

## L2 execution workers base image

```
docker build -t s2level2-jdk17 -f apps/execution/docker/Dockerfile_s2level2-jdk17 .
docker tag s2level2-jdk17 artifactory.coprs.esa-copernicus.eu/s2-docker/s2level2-jdk17:6.1.0
docker push artifactory.coprs.esa-copernicus.eu/s2-docker/s2level2-jdk17:6.1.0
```
