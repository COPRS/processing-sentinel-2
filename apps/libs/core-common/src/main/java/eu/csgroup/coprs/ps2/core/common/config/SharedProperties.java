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
@ConfigurationProperties("ps2")
public class SharedProperties {

    /**
     * Path to the root of the shared filesystem
     */
    private String sharedFolderRoot = "/shared";

    /**
     * Folder where DEM files are stored. Mount-point to shared disk.
     */
    private String demFolderRoot = "/dem";

    /**
     * Path to the root of the filesystem containing GRID files
     */
    private String gridFolderRoot = "/grid";

    /**
     * Minimum number of granules required to start L1 processing chain
     */
    private long minGrRequired = 48;

    /**
     * Maximum number of parallel processing tasks (for L1 & L2)
     */
    private int maxParallelTasks = 8;

    /**
     * Timeout in seconds for each orchestrator step to complete (for L1)
     */
    private int killTimeout = 7200;

}
