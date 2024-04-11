/*
 * Copyright 2023 CS Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.csgroup.coprs.ps2.core.obs.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Getter
@Setter
@Configuration
@ConfigurationProperties("obs")
public class ObsProperties {

    /**
     * Define Obs endpoint
     */
    private String endpoint;

    /**
     * Define Obs region
     */
    private String region;

    /**
     * Define Obs access key
     */
    private String accessKey;

    /**
     * Define Obs secret key
     */
    private String secretKey;

    /**
     * Define Obs maximum simultaneous connections; used for parallel and multipart transfers
     */
    private Integer maxConcurrency;

    /**
     * Define Obs maximum throughput, in Gb
     */
    private Double maxThroughput;

    /**
     * Define Obs minimum part size; in MB; for multipart transfers
     */
    private Long minimumPartSize;

    /**
     * Define Obs maximum number of retries on error
     */
    private int maxRetries;

    /**
     * Timeout in minutes for download operations
     */
    private int downloadTimeout;

    /**
     * Timeout in minutes for upload operations
     */
    private int uploadTimeout;

}
