# RS Addon : S2_L0c

<!-- TOC -->

* [RS Addon : S2_L0c](#rs-addon--s2l0c)
    * [Prerequisites](#prerequisites)
    * [Deployment](#deployment)
        * [Principle](#principle)
        * [Additional resources](#additional-resources)
        * [Requirements](#requirements)
    * [Configuration](#configuration)
        * [Global deployer settings](#global-deployer-settings)
        * [Workers deployer settings](#workers-deployer-settings)
        * [Filter](#filter)
        * [Router](#router)
        * [OBS settings](#obs-settings)
        * [Cleanup setting](#cleanup-setting)
        * [Kafka settings](#kafka-settings)
        * [Preparation worker](#preparation-worker)
            * [Catalog](#catalog)
            * [MongoDB](#mongodb)
            * [Misc](#misc)
        * [Execution worker](#execution-worker)

<!-- TOC -->

## Prerequisites

- Global requirements are met (See [Global readme file](../../README.md)).
- S2_L0U addon is deployed
- Credentials are available for the MongoDB server
- SCDF is configured with this standard application : filter
- Ceph-FS is configured to host the shared storage
- OBS Buckets are configured for outgoing files (L0c Datastrips and Granules)
- A shared volume is accessible and contains the required DEM_GLOBEF directory (S2IPF-DEMGLOBE)

## Deployment

### Principle

This rs-addon may be deployed either using the automated process, or manually.  
See [General installation](../../README.md#Installation).

### Additional resources

The [Additional resources](Executables/additional_resources) will create:

- Secrets for the preparation and execution workers

### Requirements

Here are the basic requirements for the main components:

| Resource          | Preparation Worker | Execution Worker |
|-------------------|:------------------:|:----------------:|
| CPU               |       2000m        |      8000m       |
| Memory            |        4Gi         |       32Gi       |
| Disk size (local) |         -          |      200GB       |
| Disk size (Ceph)  |         -          |      200GB       |

## Configuration

See [Stream parameters](Executables/stream-parameters.properties) for a full list with defaults.

### Global deployer settings

_Prefix_: deployer.*.kubernetes

| Property          | Description                |  Default   |
|-------------------|----------------------------|:----------:|
| image-pull-policy | k8s image pull policy      |   Always   |
| namespace         | k8s namespace to deploy to | processing |

### Workers deployer settings

_Prefix_: deployer.&lt;APP&gt;.kubernetes  
_Apps_: pw-l0c, ew-l0c

| Property                      | Description                            |                                                                                                 Default (pw-l0c)                                                                                                  |                                                                                                  Default (ew-l0c)                                                                                                  |
|-------------------------------|----------------------------------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| liveness-probe-delay          | Probe delay for liveness (seconds)     |                                                                                                        10                                                                                                         |                                                                                                         10                                                                                                         |
| liveness-probe-path           | Probe path for liveness                |                                                                                             /actuator/health/liveness                                                                                             |                                                                                             /actuator/health/liveness                                                                                              |
| liveness-probe-period         | Probe interval for liveness (seconds)  |                                                                                                        60                                                                                                         |                                                                                                         60                                                                                                         |
| liveness-probe-port           | Port for liveness probe                |                                                                                                       8080                                                                                                        |                                                                                                        8080                                                                                                        |
| liveness-probe-timeout        | Timeout for liveness (seconds)         |                                                                                                        60                                                                                                         |                                                                                                         60                                                                                                         |
| max-terminated-error-restarts | Max number of restarts on error        |                                                                                                         3                                                                                                         |                                                                                                         3                                                                                                          |
| readiness-probe-delay         | Probe delay for readiness (seconds)    |                                                                                                        60                                                                                                         |                                                                                                         60                                                                                                         |
| readiness-probe-path          | Probe path for readiness               |                                                                                            /actuator/health/readiness                                                                                             |                                                                                             /actuator/health/readiness                                                                                             |
| readiness-probe-period        | Probe interval for readiness (seconds) |                                                                                                        60                                                                                                         |                                                                                                         60                                                                                                         |
| readiness-probe-port          | Port for readiness probe               |                                                                                                       8080                                                                                                        |                                                                                                        8080                                                                                                        |
| readiness-probe-timeout       | Timeout for readiness (seconds)        |                                                                                                        20                                                                                                         |                                                                                                         20                                                                                                         |
| requests.memory               | Memory request                         |                                                                                                       500Mi                                                                                                       |                                                                                                       2000Mi                                                                                                       |
| limits.memory                 | Memory limit                           |                                                                                                      4000Mi                                                                                                       |                                                                                                      24000Mi                                                                                                       |
| requests.cpu                  | CPU request                            |                                                                                                        50m                                                                                                        |                                                                                                       1000m                                                                                                        |
| limits.cpu                    | CPU limit                              |                                                                                                       2000m                                                                                                       |                                                                                                       6000m                                                                                                        |
| secret-refs                   | Name of the secret to bind             |                                                                                                     s2-l0c-pw                                                                                                     |                                                                                                     s2-l0c-ew                                                                                                      |
| podSecurityContext            | Security Context                       |                                                                                                 {runAsUser: 1000}                                                                                                 |                                                                                                 {runAsUser: 1000}                                                                                                  |
| volume-mounts                 | Volume mounts                          |                                                                 [ { name: shared, mountPath: '/shared' }, <br/>{ name: dem, mountPath: '/dem' } ]                                                                 |                                                                  [ { name: input, mountPath: '/input' }, <br/>{ name: dem, mountPath: '/dem' } ]                                                                   |
| volumes                       | Volumes                                | [ { name: input, persistentVolumeClaim: <br/>{ claimName: 's2-l0-shared', storageClassName: 'ceph-fs' } }, <br/>{ name: dem, persistentVolumeClaim: <br/>{ claimName: 's2-dem', storageClassName: 'ceph-fs' } } ] | [ { name: input, persistentVolumeClaim: <br/>{ claimName: 's2-l0u-output', storageClassName: 'ceph-fs' } }, <br/>{ name: dem, persistentVolumeClaim: <br/>{ claimName: 's2-dem', storageClassName: 'ceph-fs' } } ] |

### Filter

_Prefix_: app.filter-input-l0c

| Property                                 | Description                                       |                              Default                               |
|------------------------------------------|---------------------------------------------------|:------------------------------------------------------------------:|
| spring.cloud.stream.bindings.input.group | Kafka consumer group                              |                          filter-input-l0c                          |
| expression                               | SpEL expression to filter incoming catalog events | "payload.missionId=='S2' and <br/>payload.productFamily=='S2_AUX'" |
| requests.memory                          | Memory requests                                   |                               160Mi                                |
| limits.memory                            | Memory limit                                      |                               300Mi                                |
| requests.cpu                             | CPU request                                       |                                512m                                |
| limits.cpu                               | CPU limit                                         |                                600m                                |

### Router

_Prefix_: deployer.s2-l0c-router.kubernetes

| Property        | Description     | Default |
|-----------------|-----------------|:-------:|
| requests.memory | Memory requests |  160Mi  |
| limits.memory   | Memory limit    |  300Mi  |
| requests.cpu    | CPU request     |  400m   |
| limits.cpu      | CPU limit       |  600m   |

### OBS settings

_Prefix_: app.&lt;APP&gt;.obs  
_Apps_: pw-l0c, ew-l0c

| Property             | Description                                      |                     Default (pw-l0c)                     |
|----------------------|--------------------------------------------------|:--------------------------------------------------------:|
| endpoint             | Endpoint for OBS connection                      | https://oss.eu-west-0.prod-cloud-ocb.orange-business.com |
| region               | OBS Region                                       |                        eu-west-0                         |
| maxConcurrency       | Maximum number of concurrent network connections |                            50                            |
| maxThroughput        | Maximum throughput for OBS transfers (Gb)        |                            10                            |
| minimumPartSize      | Minimum part size for multipart transfers (MB)   |                            5                             |
| maxRetries           | Maximum number of retries on error               |                            3                             |
| downloadTimeout      | Timeout in minutes for download operations       |                            15                            |
| uploadTimeout        | Timeout in minutes for upload operations         |                            15                            |
| bucket.auxBucket     | Name of the OBS bucket containing AUX files      |                        rs-s2-aux                         |
| bucket.sessionBucket | Bucket where sessions files are stored           |                     rs-session-files                     |
| bucket.l0DSBucket    | Name of the OBS bucket containing L0 DS files    |                        rs-s2-l0c                         |
| bucket.l0GRBucket    | Name of the OBS bucket containing L0 GR files    |                        rs-s2-l0c                         |

### Cleanup setting

_Prefix_: app.&lt;APP&gt;.cleanup  
_Apps_: pw-l0c, ew-l0c

| Property      | Description                                                                        | Default |
|---------------|------------------------------------------------------------------------------------|:-------:|
| localEnabled  | Enable cleaning up the local workspace folder                                      |  true   |
| sharedEnabled | Enable cleaning up old folders on the shared filesystem                            |  true   |
| 12            | Number of hours after which folder on the shared filesystem are considered expired |   12    |

### Kafka settings

_Prefix_: app.&lt;APP&gt;.spring  
_Apps_: pw-l0c, ew-l0c

| Property                                                           | Description                            |                        Default (pw-l0c)                         |                        Default (ew-l0c)                         |
|--------------------------------------------------------------------|----------------------------------------|:---------------------------------------------------------------:|:---------------------------------------------------------------:|
| kafka.bootstrap-servers                                            | URL of Kafka server                    | kafka-cluster-kafka-bootstrap<br/>.infra.svc.cluster.local:9092 | kafka-cluster-kafka-bootstrap<br/>.infra.svc.cluster.local:9092 |
| cloud.stream.kafka.binder.brokers                                  | URL of Kafka server                    | kafka-cluster-kafka-bootstrap<br/>.infra.svc.cluster.local:9092 | kafka-cluster-kafka-bootstrap<br/>.infra.svc.cluster.local:9092 |
| cloud.stream.kafka.binder.auto-create-topics                       | Enable automatic topic creation        |                              true                               |                              true                               |
| cloud.stream.kafka.binder.consumer-properties.max.poll.interval.ms | Max poll interval (ms)                 |                                -                                |                             3600000                             |
| cloud.stream.kafka.binder.consumer-properties.max.poll.records     | Max number of records per poll         |                                -                                |                                1                                |
| cloud.stream.kafka.bindings.input.consumer.enable-dlq              | Enable dlq mechanism                   |                              true                               |                              true                               |
| cloud.stream.kafka.bindings.input.consumer.dlq-name                | Name of the dlq topic                  |                          error-warning                          |                          error-warning                          |
| cloud.stream.kafka.bindings.input.consumer.poll-timeout            | Poll timeout for the Kafka consumer    |                                5                                |                                5                                |
| cloud.stream.bindings.input.consumer.max-attempts                  | Max number of retries for the consumer |                                1                                |                                1                                |
| cloud.stream.bindings.input.group                                  | Name of the Kafka consumer group       |                            s2-l0c-pw                            |                            s2-l0c-ew                            |

### Preparation worker

#### Catalog

_Prefix_: app.pw-l0c.catalog

| Property | Description                               |                                        Default                                         |
|----------|-------------------------------------------|:--------------------------------------------------------------------------------------:|
| url      | URL for the Metadata Catalog              | http://rs-metadata-catalog-searchcontroller-svc<br/>.processing.svc.cluster.local:8080 |
| timeout  | Timeout for Catalog connections (seconds) |                                           5                                            |

#### MongoDB

_Prefix_: app.pw-l0c.mongo

| Property               | Description                 |                        Default                        |
|------------------------|-----------------------------|:-----------------------------------------------------:|
| authenticationDatabase | Authentication database     |                         admin                         |
| database               | Name of the database to use |                       s2-l0c-pw                       |
| port                   | Port for connection         |                         27017                         |
| host                   | Server url                  | mongodb-0.mongodb-headless.database.svc.cluster.local |

#### Misc

_Prefix_: app.pw-l0c

| Property               | Description                                                            | Default |
|------------------------|------------------------------------------------------------------------|:-------:|
| spring.profiles.active | Name of the profile to run with (prod or dev)                          |  prod   |
| ps2.sharedFolderRoot   | Path to the folder used as input for L0u files.<br/>Mount to shared fs | /shared |
| ps2.demFolderRoot      | Path to the folder holding DEM_GLOBEF folder                           |  /dem   |

### Execution worker

_Prefix_: app.ew-l0c

| Property               | Description                                   | Default |
|------------------------|-----------------------------------------------|:-------:|
| spring.profiles.active | Name of the profile to run with (prod or dev) |  prod   |

----
