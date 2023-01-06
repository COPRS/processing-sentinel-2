# RS Addon : S2_L2

<!-- TOC -->

* [RS Addon : S2_L2](#rs-addon--s2_l2)
    * [Prerequisites](#prerequisites)
    * [Deployment](#deployment)
        * [Principle](#principle)
        * [Additional resources](#additional-resources)
        * [Sizing](#sizing)
    * [Configuration](#configuration)
        * [Global deployer settings](#global-deployer-settings)
        * [Workers deployer settings](#workers-deployer-settings)
        * [Workers volume mounts](#workers-volume-mounts)
        * [Filter](#filter)
        * [Router](#router)
        * [OBS settings](#obs-settings)
        * [Cleanup setting](#cleanup-setting)
        * [Kafka settings](#kafka-settings)
        * [Catalog](#catalog)
        * [MongoDB](#mongodb)
        * [Misc](#misc)
        * [Preparation workers](#preparation-workers)
        * [Execution workers](#execution-workers)

<!-- TOC -->

## Prerequisites

- Global requirements are met (See [Global readme file](../../README.md)).
- Credentials are available for the MongoDB server
- Ceph-FS is configured to host the shared storage
- OBS Buckets are configured for incoming (AUX files and L1c products) and outgoing (L2 products) files
- A shared volume is accessible and contains the required DEM directories (S2IPF-DEML2, S2IPF-ESACCI)

## Deployment

### Principle

This rs-addon may be deployed either using the automated process, or manually.  
See [General installation](../../README.md#Installation).

### Additional resources

The [Additional resources](Executables/additional_resources) will create:

- A Kafka topic for communication between internal containers
- A shared volume to store intermediary products
- Secrets for the preparation and execution workers

### Sizing

Here are the basic sizing suggestions for the main components:

| Resource                | Preparation Worker | Execution Worker |
|-------------------------|:------------------:|:----------------:|
| CPU                     |       2000m        |      8000m       |
| Memory                  |        4Gi         |       32Gi       |
| Disk size (local)       |         -          |      500GB       |
| Shared disk size (Ceph) |         -          |      500GB       |

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
_Apps_: pw-l2, ew-l2-ds, ew-l2-tl

| Property                      | Description                            |      Default (pw-l*)       |      Default (ew-l*)       |
|-------------------------------|----------------------------------------|:--------------------------:|:--------------------------:|
| liveness-probe-delay          | Probe delay for liveness (seconds)     |             10             |             10             |
| liveness-probe-path           | Probe path for liveness                | /actuator/health/liveness  | /actuator/health/liveness  |
| liveness-probe-period         | Probe interval for liveness (seconds)  |             60             |             60             |
| liveness-probe-port           | Port for liveness probe                |            8080            |            8080            |
| liveness-probe-timeout        | Timeout for liveness (seconds)         |             60             |             60             |
| max-terminated-error-restarts | Max number of restarts on error        |             3              |             3              |
| readiness-probe-delay         | Probe delay for readiness (seconds)    |             60             |             60             |
| readiness-probe-path          | Probe path for readiness               | /actuator/health/readiness | /actuator/health/readiness |
| readiness-probe-period        | Probe interval for readiness (seconds) |             60             |             60             |
| readiness-probe-port          | Port for readiness probe               |            8080            |            8080            |
| readiness-probe-timeout       | Timeout for readiness (seconds)        |             20             |             20             |
| requests.memory               | Memory requets                         |           1000Mi           |           2000Mi           |
| limits.memory                 | Memory limit                           |           4000Mi           |          32000Mi           |
| requests.cpu                  | CPU request                            |            300m            |           1000m            |
| limits.cpu                    | CPU limit                              |           2000m            |           8000m            |
| secret-refs                   | Name of the secrets to bind            | [ s2-l2-mongo, s2-l2-obs ] |         s2-l2-obs          |
| podSecurityContext            | Security Context                       |     {runAsUser: 1000}      |     {runAsUser: 1000}      |

### Workers volume mounts

_Apps_: pw-l2

| Property                                 | Description                |                                                   Default                                                   |
|------------------------------------------|:---------------------------|:-----------------------------------------------------------------------------------------------------------:|
| deployer.\<APP>.kubernetes.volume-mounts | List of volume mounts      |                                 [ { name: shared, mountPath: '/shared' } ]                                  |
| deployer.\<APP>.kubernetes.volumes       | List of volume definitions | [ { name: shared, persistentVolumeClaim:<br> { claimName: 's2-l1-shared', storageClassName: 'ceph-fs' } } ] |

_Apps_: ew-l2-ds, ew-l2-tl

| Property                                 | Description                |                                                                                                   Default                                                                                                   |
|------------------------------------------|:---------------------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| deployer.\<APP>.kubernetes.volume-mounts | List of volume mounts      |                                                              [ { name: shared, mountPath: '/shared' },<br/> { name: dem, mountPath: '/dem' } ]                                                              |
| deployer.\<APP>.kubernetes.volumes       | List of volume definitions | [ { name: shared, persistentVolumeClaim:<br> { claimName: 's2-l2-shared', storageClassName: 'ceph-fs' } },<br> { name: dem, persistentVolumeClaim: { claimName: 's2-dem', storageClassName: 'ceph-fs' } } ] |

### Filter

_Prefix_: app.s2-l2-filter

| Property                                 | Description                                       |                                                                         Default                                                                         |
|------------------------------------------|---------------------------------------------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------:|
| spring.cloud.stream.bindings.input.group | Kafka consumer group                              |                                                                      s2-l2-filter                                                                       |
| expression                               | SpEL expression to filter incoming catalog events | (payload.missionId=='S2' and<br> (payload.productFamily=='S2_L1C_DS'<br> or payload.productFamily=='S2_L1C_TL'<br> or payload.productFamily=='S2_AUX')) |

### Router

_Prefix_: app.s2-l2-router

| Property                                 | Description                                          |         Default         |
|------------------------------------------|------------------------------------------------------|:-----------------------:|
| spring.cloud.stream.bindings.input.group | Kafka consumer group                                 |      s2-l2-router       |
| refresh-delay                            | Delay for the refresh of the router script (seconds) |           30            |
| script                                   | Path to the router script on the local fs            | file:/etc/router.groovy |

_Prefix_: deployer.s2-l2-router.kubernetes

| Property      | Description        |                                    Default                                     |
|---------------|--------------------|:------------------------------------------------------------------------------:|
| volume-mounts | Mounted volumes    | [ {name: script, mountPath: '/etc/router.groovy', subPath: 'router.groovy' } ] |
| volumes       | Volumes definition |         [ {name: script, configmap: { name: s2-l2-router-script } } ]          |

### OBS settings

_Prefix_: app.&lt;APP&gt;.obs  
_Apps_: pw-l2, ew-l2-ds, ew-l2-tl

| Property          | Description                                      |                         Default                          |
|-------------------|--------------------------------------------------|:--------------------------------------------------------:|
| endpoint          | Endpoint for OBS connection                      | https://oss.eu-west-0.prod-cloud-ocb.orange-business.com |
| region            | OBS Region                                       |                        eu-west-0                         |
| maxConcurrency    | Maximum number of concurrent network connections |                            50                            |
| maxThroughput     | Maximum throughput for OBS transfers (Gb)        |                            10                            |
| minimumPartSize   | Minimum part size for multipart transfers (MB)   |                            5                             |
| auxBucket         | Name of the OBS bucket containing AUX files      |                        rs-s2-aux                         |
| bucket.l1DSBucket | Name of the OBS bucket containing L1 DS files    |                         rs-s2-l1                         |
| bucket.l1TLBucket | Name of the OBS bucket containing L1 TL files    |                         rs-s2-l1                         |

### Cleanup setting

_Prefix_: app.&lt;APP&gt;.cleanup  
_Apps_: pw-l2, ew-l2-ds, ew-l2-tl

| Property      | Description                                                                        | Default |
|---------------|------------------------------------------------------------------------------------|:-------:|
| localEnabled  | Enable cleaning up the local workspace folder                                      |  true   |
| sharedEnabled | Enable cleaning up old folders on the shared filesystem                            |  true   |
| 12            | Number of hours after which folder on the shared filesystem are considered expired |   12    |

### Kafka settings

_Prefix_: app.&lt;APP&gt;.spring  
_Apps_: pw-l2, ew-l2-ds, ew-l2-tl

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
| cloud.stream.bindings.input.group                                  | Name of the Kafka consumer group       |                          s2-l2-\<APP>                           |                          s2-l2-\<APP>                           |

### Catalog

_Prefix_: app.&lt;APP&gt;.catalog  
_Apps_: pw-l2

| Property | Description                               |                                        Default                                         |
|----------|-------------------------------------------|:--------------------------------------------------------------------------------------:|
| url      | URL for the Metadata Catalog              | http://rs-metadata-catalog-searchcontroller-svc<br/>.processing.svc.cluster.local:8080 |
| timeout  | Timeout for Catalog connections (seconds) |                                           5                                            |

### MongoDB

_Prefix_: app.&lt;APP&gt;.mongo  
_Apps_: pw-l2

| Property               | Description                 |                        Default                        |
|------------------------|-----------------------------|:-----------------------------------------------------:|
| authenticationDatabase | Authentication database     |                         admin                         |
| database               | Name of the database to use |                     s1-l2-\<APP>                      |
| port                   | Port for connection         |                         27017                         |
| host                   | Server url                  | mongodb-0.mongodb-headless.database.svc.cluster.local |

### Misc

_Prefix_: app.&lt;APP&gt;  
_Apps_: pw-l2, ew-l2-ds, ew-l2-tl

| Property               | Description                                   | Default |
|------------------------|-----------------------------------------------|:-------:|
| spring.profiles.active | Name of the profile to run with (prod or dev) |  prod   |

### Preparation workers

_Apps_: pw-l2

| Property                       | Description                                    |  Default  |
|--------------------------------|------------------------------------------------|:---------:|
| app.pw-l1s.pw.sharedFolderRoot | Path to the shared folder for L1 working files |  /shared  |

### Execution workers

_Prefix_: app.&lt;APP&gt;.ps2;  
_Apps_: ew-l2-ds, ew-l2-tl

| Property         | Description                                    | Default |
|------------------|------------------------------------------------|:-------:|
| sharedFolderRoot | Path to the shared folder for L1 working files | /shared |
| demFolderRoot    | Path to the folder for DEM files               |  /dem   |
| maxParallelTasks | Maximum number of parallel processing tasks    |    8    |

----
