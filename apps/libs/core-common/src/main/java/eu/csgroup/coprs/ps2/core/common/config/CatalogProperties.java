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

package eu.csgroup.coprs.ps2.core.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("catalog")
public class CatalogProperties {

    /**
     * URL for the Metadata Catalog
     */
    private String url;

    /**
     * Timeout for Metadata Catalog connections
     */
    private int timeout = 5;

    /**
     * Product Family for AUX queries
     */
    private String auxProductFamily = "S2_AUX";

    /**
     * Mode for AUX metadata request
     */
    private String mode = "LatestValCover";

    /**
     * Maximum retry count for Metadata Catalog queries
     */
    private int maxRetry = 3;

}
