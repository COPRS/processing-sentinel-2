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

package eu.csgroup.coprs.ps2.core.ew.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("missing")
public class MissingOutputProperties {

    /**
     * Version of the IPF script used for level 0u processing
     */
    private String l0uIpfVersion;

    /**
     * Version of the IPF script used for level 0c processing
     */
    private String l0cIpfVersion;

    /**
     * Version of the IPF script used for level 1 processing
     */
    private String l1IpfVersion;

    /**
     * Version of the IPF script used for level 2 processing
     */
    private String l2IpfVersion;

    /**
     * Ratio representing an (arbitrary) average number of granules per Tile.
     */
    private double grToTlRatio;

    /**
     * Default number of missing L0 Datastrip.
     */
    private int l0uDefaultDsCount;

    /**
     * Arbitrary number of missing HouseKeeping TeleMetry (HKTM) auxiliary files.
     */
    private int l0uDefaultHktmCount;

    /**
     * Arbitrary number of missing Satellite Ancillary Data (SAD) auxiliary files.
     */
    private int l0uDefaultSadCount;

    /**
     * Arbitrary number of missing Granules when L0c processing fails and it is unable to count the granules.
     */
    private int l0cDefaultGrCount;

}
