
## L0u execution worker base image

```
docker build -t s2-ipf-l0u-jdk17 -f Dockerfile_s2-ipf-l0u-jdk17 .
docker tag s2-ipf-l0u-jdk17 artifactory.coprs.esa-copernicus.eu/s2-docker/s2-ipf-l0u-jdk17:3.0.1
docker push artifactory.coprs.esa-copernicus.eu/s2-docker/s2-ipf-l0u-jdk17:3.0.1 
```

## L0c execution worker base image

```
docker build -t s2level0-jdk17 -f Dockerfile_s2level0-jdk17 .
docker tag s2level0-jdk17 artifactory.coprs.esa-copernicus.eu/s2-docker/s2level0-jdk17:5.1.0
docker push artifactory.coprs.esa-copernicus.eu/s2-docker/s2level0-jdk17:5.1.0 
```

## L1 execution workers base image

```
docker build -t s2level1-jdk17 -f Dockerfile_s2level1-jdk17 .
docker tag s2level1-jdk17 artifactory.coprs.esa-copernicus.eu/s2-docker/s2level1-jdk17:6.1.0
docker push artifactory.coprs.esa-copernicus.eu/s2-docker/s2level1-jdk17:6.1.0
```

## L2 execution workers base image

```
docker build -t s2level2-jdk17 -f Dockerfile_s2level2-jdk17 .
docker tag s2level2-jdk17 artifactory.coprs.esa-copernicus.eu/s2-docker/s2level2-jdk17:6.1.0
docker push artifactory.coprs.esa-copernicus.eu/s2-docker/s2level2-jdk17:6.1.0
```
