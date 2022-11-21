# RS Addon : S2_L0u

<!-- TOC -->
* [RS Addon : S2_L0u](#rs-addon--s2_l0u)
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
    * [Kafka settings](#kafka-settings)
    * [Preparation worker](#preparation-worker)
      * [Catalog](#catalog)
      * [MongoDB](#mongodb)
      * [Misc](#misc)
    * [Execution worker](#execution-worker)
<!-- TOC -->

## Prerequisites

- Global requirements are met (See [Global readme file](../../README.md)).
- Credentials are available for the MongoDB server
- SCDF is configured with these standard applications : filter and router
- Ceph-FS is configured to host the shared storage
- OBS Buckets are configured for incoming (Sessions and AUX) and outgoing (SAD and HKTM) files

## Deployment

### Principle

This rs-addon may be deployed either using the automated process, or manually.  
See [General installation](../../README.md#Installation).

### Additional resources

The [Additional resources](Executables/additional_resources) will create:

- A Configmap for router configuration
- A Kafka topic for communication between S2_L0u and S2_L0c
- A shared volume to store the L0u output, later used by the S2_L0c addon
- Secrets for the preparation and execution workers

### Requirements

Here are the basic requirements for the main components:

| Resource          |  Preparation Worker  |  Execution Worker  |
|-------------------|:--------------------:|:------------------:|
| CPU               |        2000m         |       8000m        |
| Memory            |         4Gi          |        24Gi        |
| Disk size (local) |          -           |       200GB        |
| Disk size (Ceph)  |          -           |       200GB        |

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
_Apps_: pw-l0u, ew-l0u

| Property                         | Description                            |      Default (pw-l0u)      |                                               Default (ew-l0u)                                                |
|----------------------------------|----------------------------------------|:--------------------------:|:-------------------------------------------------------------------------------------------------------------:|
| liveness-probe-delay             | Probe delay for liveness (seconds)     |             10             |                                                      10                                                       |
| liveness-probe-path              | Probe path for liveness                | /actuator/health/liveness  |                                           /actuator/health/liveness                                           |
| liveness-probe-period            | Probe interval for liveness (seconds)  |             60             |                                                      60                                                       |
| liveness-probe-port              | Port for liveness probe                |            8080            |                                                     8080                                                      |
| liveness-probe-timeout           | Timeout for liveness (seconds)         |             60             |                                                      60                                                       |
| max-terminated-error-restarts    | Max number of restarts on error        |             3              |                                                       3                                                       |
| readiness-probe-delay            | Probe delay for readiness (seconds)    |             60             |                                                      60                                                       |
| readiness-probe-path             | Probe path for readiness               | /actuator/health/readiness |                                          /actuator/health/readiness                                           |
| readiness-probe-period           | Probe interval for readiness (seconds) |             60             |                                                      60                                                       |
| readiness-probe-port             | Port for readiness probe               |            8080            |                                                     8080                                                      |
| readiness-probe-timeout          | Timeout for readiness (seconds)        |             20             |                                                      20                                                       |
| requests.memory                  | Memory requets                         |           2000Mi           |                                                    2000Mi                                                     |
| requests.cpu                     | CPU request                            |            300m            |                                                     300m                                                      |
| limits.memory                    | Memory limit                           |           4000Mi           |                                                    24000Mi                                                    |
| limits.cpu                       | CPU limit                              |           2000m            |                                                     8000m                                                     |
| secret-refs                      | Name of the secret to bind             |         s2-l0u-pw          |                                                   s2-l0u-ew                                                   |
| pod-security-context.run-as-user | UID to run the app as                  |            1000            |                                                     1001                                                      |
| volume-mounts                    | Volume mounts                          |             -              |                                  [ { name: output, mountPath: '/output' } ]                                   |
| volumes                          | Volumes                                |             -              | [ { name: output, persistentVolumeClaim: <br/>{ claimName: 's2-l0u-output', storageClassName: 'ceph-fs' } } ] |

### Filter

_Prefix_: app.filter-input-l0u

| Property                                 | Description                                       |                                                       Default                                                       |
|------------------------------------------|---------------------------------------------------|:-------------------------------------------------------------------------------------------------------------------:|
| spring.cloud.stream.bindings.input.group | Kafka consumer group                              |                                                  filter-input-l0u                                                   |
| expression                               | SpEL expression to filter incoming catalog events | "payload.missionId=='S2' and <br />(payload.productFamily=='S2_AUX' <br/>or payload.productFamily=='EDRS_SESSION')" |

### Router

_Prefix_: app.router-output-l0u

| Property                                 | Description                                          |          Default           |
|------------------------------------------|------------------------------------------------------|:--------------------------:|
| spring.cloud.stream.bindings.input.group | Kafka consumer group                                 |     router-output-l0u      |
| refresh-delay                            | Delay for the refresh of the router script (seconds) |             30             |
| script                                   | Path to the router script on the local fs            | file:/etc/router-ew.groovy |

_Prefix_: deployer.router-output-l0u.kubernetes

| Property      | Description        |                                       Default                                        |
|---------------|--------------------|:------------------------------------------------------------------------------------:|
| volume-mounts | Mounted volumes    | [ {name: script, mountPath: '/etc/router-ew.groovy', subPath: 'router-ew.groovy' } ] |
| volumes       | Volumes definition |          [ {name: script, configmap: { name: s2-l0u-router-ew-script } } ]           |

### OBS settings

_Prefix_: app.&lt;APP&gt;.obs  
_Apps_: pw-l0u, ew-l0u

| Property        | Description                                      |                         Default                          |
|-----------------|--------------------------------------------------|:--------------------------------------------------------:|
| endpoint        | Endpoint for OBS connection                      | https://oss.eu-west-0.prod-cloud-ocb.orange-business.com |
| region          | OBS Region                                       |                        eu-west-0                         |
| maxConcurrency  | Maximum number of concurrent network connections |                            50                            |
| maxThroughput   | Maximum throughput for OBS transfers (Gb)        |                            10                            |
| minimumPartSize | Minimum part size for multipart transfers (MB)   |                            5                             |
| auxBucket       | Name of the OBS bucket containing AUX files      |                        rs-s2-aux                         |
| sessionBucket   | Bucket where sessions files are stored           |                     rs-session-files                     |
| sadBucket       | Name of the OBS bucket containing SAD files      |                        rs-s2-aux                         |
| hktmBucket      | Name of the OBS bucket containing HKTM files     |                        rs-s2-hktm                        |

### Cleanup setting

_Prefix_: app.&lt;APP&gt;.cleanup  
_Apps_: pw-l0u, ew-l0u

| Property      | Description                                                                        | Default |
|---------------|------------------------------------------------------------------------------------|:-------:|
| localEnabled  | Enable cleaning up the local workspace folder                                      |  true   |
| sharedEnabled | Enable cleaning up old folders on the shared filesystem                            |  true   |
| 12            | Number of hours after which folder on the shared filesystem are considered expired |   12    |

### Kafka settings

_Prefix_: app.&lt;APP&gt;.spring  
_Apps_: pw-l0u, ew-l0u

| Property                                                           | Description                            |                        Default (pw-l0u)                         |                        Default (ew-l0u)                         |
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
| cloud.stream.bindings.input.group                                  | Name of the Kafka consumer group       |                            s2-l0u-pw                            |                            s2-l0u-ew                            |

### Preparation worker

#### Catalog

_Prefix_: app.pw-l0u.catalog

| Property | Description                               |                                        Default                                         |
|----------|-------------------------------------------|:--------------------------------------------------------------------------------------:|
| url      | URL for the Metadata Catalog              | http://rs-metadata-catalog-searchcontroller-svc<br/>.processing.svc.cluster.local:8080 |
| timeout  | Timeout for Catalog connections (seconds) |                                           5                                            |

#### MongoDB

_Prefix_: app.pw-l0u.mongo

| Property               | Description                 |                        Default                        |
|------------------------|-----------------------------|:-----------------------------------------------------:|
| authenticationDatabase | Authentication database     |                         admin                         |
| database               | Name of the database to use |                       s2-l0u-pw                       |
| port                   | Port for connection         |                         27017                         |
| host                   | Server url                  | mongodb-0.mongodb-headless.database.svc.cluster.local |

#### Misc

_Prefix_: app.pw-l0u

| Property               | Description                                                |     Default      |
|------------------------|------------------------------------------------------------|:----------------:|
| spring.profiles.active | Name of the profile to run with (prod or dev)              |       prod       |

### Execution worker

_Prefix_: app.ew-l0u

| Property                | Description                                                             |  Default   |
|-------------------------|-------------------------------------------------------------------------|:----------:|
| spring.profiles.active  | Name of the profile to run with (prod or dev)                           |    prod    |
| ew.l0u.outputFolderRoot | Path to the folder used as output for L0u files.<br/>Mount to shared fs |  /output   |

----
