# RS Addon : S2_L1

<!-- TOC -->

* [RS Addon : S2_L1](#rs-addon--s2_l1)
    * [Prerequisites](#prerequisites)
    * [Deployment](#deployment)
        * [Principle](#principle)
        * [Additional resources](#additional-resources)
        * [Requirements](#requirements)
    * [Configuration](#configuration)
        * [Global deployer settings](#global-deployer-settings)
        * [Workers deployer settings](#workers-deployer-settings)
        * [Workers volume mounts](#workers-volume-mounts)
        * [Filter](#filter)
        * [OBS settings](#obs-settings)
        * [Kafka settings](#kafka-settings)
        * [Catalog](#catalog)
        * [MongoDB](#mongodb)
        * [Misc](#misc)
        * [Preparation workers](#preparation-workers)
        * [Execution workers](#execution-workers)

<!-- TOC -->[//]: # (TODO TOC)

## Prerequisites

- Global requirements are met (See [Global readme file](../../README.md)).
- Credentials are available for the MongoDB server
- Ceph-FS is configured to host the shared storage
- OBS Buckets are configured for incoming (AUX files and L0c products) and outgoing (L1 products) files

## Deployment

### Principle

This rs-addon may be deployed either using the automated process, or manually.  
See [General installation](../../README.md#Installation).

### Additional resources

The [Additional resources](Executables/additional_resources) will create:

- A Kafka topic for communication between internal containers
- A shared volume to store intermediary products
- Secrets for the preparation and execution workers

### Requirements

Here are the basic requirements for the main components:

| Resource          | Preparation Workers | Execution Workers |
|-------------------|:-------------------:|:-----------------:|
| CPU               |        2000m        |       8000m       |
| Memory            |         4Gi         |       24Gi        |
| Disk size (local) |          -          |       500GB       |
| Disk size (Ceph)  |          -          |       500GB       |

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
_Apps_: pw-l1s, ew-l1s, ew-l1ab, pw-l1c, ew-l1c

| Property                         | Description                            |      Default (pw-l*)       |      Default (ew-l*)       |
|----------------------------------|----------------------------------------|:--------------------------:|:--------------------------:|
| liveness-probe-delay             | Probe delay for liveness (seconds)     |             10             |             10             |
| liveness-probe-path              | Probe path for liveness                | /actuator/health/liveness  | /actuator/health/liveness  |
| liveness-probe-period            | Probe interval for liveness (seconds)  |             60             |             60             |
| liveness-probe-port              | Port for liveness probe                |            8080            |            8080            |
| liveness-probe-timeout           | Timeout for liveness (seconds)         |             60             |             60             |
| max-terminated-error-restarts    | Max number of restarts on error        |             3              |             3              |
| readiness-probe-delay            | Probe delay for readiness (seconds)    |             60             |             60             |
| readiness-probe-path             | Probe path for readiness               | /actuator/health/readiness | /actuator/health/readiness |
| readiness-probe-period           | Probe interval for readiness (seconds) |             60             |             60             |
| readiness-probe-port             | Port for readiness probe               |            8080            |            8080            |
| readiness-probe-timeout          | Timeout for readiness (seconds)        |             20             |             20             |
| requests.memory                  | Memory requets                         |           2000Mi           |           2000Mi           |
| requests.cpu                     | CPU request                            |            300m            |            300m            |
| limits.memory                    | Memory limit                           |           4000Mi           |          24000Mi           |
| limits.cpu                       | CPU limit                              |           2000m            |           8000m            |
| secret-refs                      | Name of the secrets to bind            | [ s2-l1-mongo, s2-l1-obs ] |         s2-l1-obs          |
| pod-security-context.run-as-user | UID to run the app as                  |            1000            |            1001            |

### Workers volume mounts

_Apps_: pw-l1s, pw-l1c

| Property                                 | Description                |                                                   Default                                                   |
|------------------------------------------|:---------------------------|:-----------------------------------------------------------------------------------------------------------:|
| deployer.\<APP>.kubernetes.volume-mounts | List of volume mounts      |                                 [ { name: shared, mountPath: '/shared' } ]                                  |
| deployer.\<APP>.kubernetes.volumes       | List of volume definitions | [ { name: shared, persistentVolumeClaim:<br> { claimName: 's2-l1-shared', storageClassName: 'ceph-fs' } } ] |

_Apps_: ew-l1s, ew-l1ab, ew-l1c

| Property                                 | Description                |                                                                                                                                                    Default                                                                                                                                                    |
|------------------------------------------|:---------------------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| deployer.\<APP>.kubernetes.volume-mounts | List of volume mounts      |                                                                                                               [ { name: shared, mountPath: '/shared' },<br/> { name: dem, mountPath: '/dem' } ]                                                                                                               |
| deployer.\<APP>.kubernetes.volumes       | List of volume definitions | [ { name: shared, persistentVolumeClaim:<br> { claimName: 's2-l1-shared', storageClassName: 'ceph-fs' } },<br> { name: dem, persistentVolumeClaim: { claimName: 's2-dem', storageClassName: 'ceph-fs' } },<br> { name: grid, persistentVolumeClaim: { claimName: 's2-grid', storageClassName: 'ceph-fs' } } ] |

### Filter

_Prefix_: app.s2-l1-filter

| Property                                 | Description                                       |                                                                        Default                                                                        |
|------------------------------------------|---------------------------------------------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------:|
| spring.cloud.stream.bindings.input.group | Kafka consumer group                              |                                                                     s2-l1-filter                                                                      |
| expression                               | SpEL expression to filter incoming catalog events | (payload.missionId=='S2' and<br> (payload.productFamily=='S2_L0_DS'<br> or payload.productFamily=='S2_L0_GR'<br> or payload.productFamily=='S2_AUX')) |

### OBS settings

_Prefix_: app.&lt;APP&gt;.obs  
_Apps_: pw-l1s, ew-l1ab, pw-l1c, ew-l1c

| Property        | Description                                      |                         Default                          |
|-----------------|--------------------------------------------------|:--------------------------------------------------------:|
| endpoint        | Endpoint for OBS connection                      | https://oss.eu-west-0.prod-cloud-ocb.orange-business.com |
| region          | OBS Region                                       |                        eu-west-0                         |
| maxConcurrency  | Maximum number of concurrent network connections |                            50                            |
| maxThroughput   | Maximum throughput for OBS transfers (Gb)        |                            10                            |
| minimumPartSize | Minimum part size for multipart transfers (MB)   |                            5                             |

### Kafka settings

_Prefix_: app.&lt;APP&gt;.spring  
_Apps_: pw-l1s, ew-l1s, ew-l1ab, pw-l1c, ew-l1c

| Property                                                           | Description                            |                         Default (pw-l*)                         |                         Default (ew-l*)                         |
|--------------------------------------------------------------------|----------------------------------------|:---------------------------------------------------------------:|:---------------------------------------------------------------:|
| kafka.bootstrap-servers                                            | URL of Kafka server                    | kafka-cluster-kafka-bootstrap<br/>.infra.svc.cluster.local:9092 | kafka-cluster-kafka-bootstrap<br/>.infra.svc.cluster.local:9092 |
| cloud.stream.kafka.binder.brokers                                  | URL of Kafka server                    | kafka-cluster-kafka-bootstrap<br/>.infra.svc.cluster.local:9092 | kafka-cluster-kafka-bootstrap<br/>.infra.svc.cluster.local:9092 |
| cloud.stream.kafka.binder.auto-create-topics                       | Enable automatic topic creation        |                              true                               |                              true                               |
| cloud.stream.kafka.binder.consumer-properties.max.poll.interval.ms | Max poll interval (ms)                 |                                -                                |                            14400000                             |
| cloud.stream.kafka.binder.consumer-properties.max.poll.records     | Max number of records per poll         |                                -                                |                                1                                |
| cloud.stream.kafka.bindings.input.consumer.enable-dlq              | Enable dlq mechanism                   |                              true                               |                              true                               |
| cloud.stream.kafka.bindings.input.consumer.dlq-name                | Name of the dlq topic                  |                          error-warning                          |                          error-warning                          |
| cloud.stream.kafka.bindings.input.consumer.poll-timeout            | Poll timeout for the Kafka consumer    |                                5                                |                                5                                |
| cloud.stream.bindings.input.consumer.max-attempts                  | Max number of retries for the consumer |                                1                                |                                1                                |
| cloud.stream.bindings.input.group                                  | Name of the Kafka consumer group       |                          s2-l1-\<APP>                           |                          s2-l1-\<APP>                           |

### Catalog

_Prefix_: app.&lt;APP&gt;.catalog  
_Apps_: pw-l1s, pw-l1c

| Property | Description                               |                                        Default                                         |
|----------|-------------------------------------------|:--------------------------------------------------------------------------------------:|
| url      | URL for the Metadata Catalog              | http://rs-metadata-catalog-searchcontroller-svc<br/>.processing.svc.cluster.local:8080 |
| timeout  | Timeout for Catalog connections (seconds) |                                           5                                            |

### MongoDB

_Prefix_: app.&lt;APP&gt;.mongo  
_Apps_: pw-l1s, pw-l1c

| Property               | Description                 |                        Default                        |
|------------------------|-----------------------------|:-----------------------------------------------------:|
| authenticationDatabase | Authentication database     |                         admin                         |
| database               | Name of the database to use |                     s1-l1-\<APP>                      |
| port                   | Port for connection         |                         27017                         |
| host                   | Server url                  | mongodb-0.mongodb-headless.database.svc.cluster.local |

### Misc

_Prefix_: app.&lt;APP&gt;  
_Apps_: pw-l1s, ew-l1s, ew-l1ab, pw-l1c, ew-l1c

| Property               | Description                                   | Default |
|------------------------|-----------------------------------------------|:-------:|
| spring.profiles.active | Name of the profile to run with (prod or dev) |  prod   |

### Preparation workers

_Prefix_: app.&lt;APP&gt;  
_Apps_: pw-l1s, pw-l1c

| Property                       | Description                                    |  Default  |
|--------------------------------|------------------------------------------------|:---------:|
| app.pw-l1s.pw.auxBucket        | Name of the OBS bucket containing AUX files    | rs-s2-aux |
| app.pw-l1s.pw.l0DSBucket       | Name of the OBS bucket containing L0c DS files | rs-s2-l0c |
| app.pw-l1s.pw.l0GRBucket       | Name of the OBS bucket containing L0c GR files | rs-s2-l0c |
| app.pw-l1s.pw.sharedFolderRoot | Path to the shared folder for L1 working files |  /shared  |
||||
| app.pw-l1c.pw.auxBucket        | Name of the OBS bucket containing AUX files    | rs-s2-aux |
| app.pw-l1c.ew.sharedFolderRoot | Path to the shared folder for L1 working files |  /shared  |

### Execution workers

_Prefix_: app.&lt;APP&gt;  
_Apps_: ew-l1s, ew-l1ab, ew-l1c

| Property                        | Description                                    |  Default  |
|---------------------------------|------------------------------------------------|:---------:|
||||
| app.ew-l1s.ew.auxBucket         | Name of the OBS bucket containing AUX files    | rs-s2-aux |
| app.ew-l1s.ew.sharedFolderRoot  | Path to the shared folder for L1 working files |  /shared  |
| app.ew-l1s.ew.demFolderRoot     | Path to the folder for DEM files               |   /dem    |
| app.ew-l1s.ew.gridFolderRoot    | Path to the folder for GRID files              |   /grid   |
| app.ew-l1s.ew.maxParallelTasks  | Maximum number of parallel processing tasks    |     8     |
||||
| app.ew-l1ab.ew.auxBucket        | Name of the OBS bucket containing AUX files    | rs-s2-aux |
| app.ew-l1ab.ew.l1DSBucket       | Name of the OBS bucket to store L1ab DS files  | rs-s2-l1  |
| app.ew-l1ab.ew.l1GRBucket       | Name of the OBS bucket to store L1ab GR files  | rs-s2-l1  |
| app.ew-l1ab.ew.sharedFolderRoot | Path to the shared folder for L1 working files |  /shared  |
| app.ew-l1ab.ew.demFolderRoot    | Path to the folder for DEM files               |   /dem    |
| app.ew-l1ab.ew.gridFolderRoot   | Path to the folder for GRID files              |   /grid   |
| app.ew-l1ab.ew.maxParallelTasks | Maximum number of parallel processing tasks    |     8     |
||||
| app.ew-l1c.ew.auxBucket         | Name of the OBS bucket containing AUX files    | rs-s2-aux |
| app.ew-l1c.ew.l1DSBucket        | Name of the OBS bucket to store L1c DS files   | rs-s2-l1  |
| app.ew-l1c.ew.l1TLBucket        | Name of the OBS bucket to store L1c TL files   | rs-s2-l1  |
| app.ew-l1c.ew.sharedFolderRoot  | Path to the shared folder for L1 working files |  /shared  |
| app.ew-l1c.ew.demFolderRoot     | Path to the folder for DEM files               |   /dem    |
| app.ew-l1c.ew.gridFolderRoot    | Path to the folder for GRID files              |   /grid   |
| app.ew-l1c.ew.maxParallelTasks  | Maximum number of parallel processing tasks    |     8     |

----
