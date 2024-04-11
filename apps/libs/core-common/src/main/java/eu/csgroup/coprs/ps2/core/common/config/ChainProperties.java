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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("chain")
public class ChainProperties {

    private String name;
    private String version;

    // Below :
    // Quick and dirty Workaround for trace headers and missing outputs
    // Should be refactored later with a cleaner solution

    private static String CHAIN_NAME; // NOSONAR
    private static String CHAIN_VERSION; // NOSONAR

    @Value("${chain.name}")
    public void setNameStatic(String name) {
        ChainProperties.CHAIN_NAME = name; // NOSONAR
    }

    @Value("${chain.version}")
    public void setVersionStatic(String version) {
        ChainProperties.CHAIN_VERSION = version; // NOSONAR
    }

    public static String getChainName() {
        return CHAIN_NAME;
    }

    public static String getChainVersion() {
        return CHAIN_VERSION;
    }

}
