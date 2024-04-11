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

package eu.csgroup.coprs.ps2.core.common.utils;

import eu.csgroup.coprs.ps2.core.common.exception.ScriptExecutionException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScriptLogger implements Runnable {

    private InputStream standardStream;
    private List<String> whitelist;

    @Override
    public void run() {
        try (BufferedReader standardBufferReader = new BufferedReader(new InputStreamReader(standardStream))) {
            standardBufferReader.lines().forEach(logger());
        } catch (Exception e) {
            throw new ScriptExecutionException("Unable to process script logs", e);
        }
    }

    private Consumer<String> logger() {
        if (CollectionUtils.isEmpty(whitelist)) {
            return log::info;
        } else return string -> {
            for (String marker : whitelist) {
                if (string.contains(marker)) {
                    log.info(string);
                    return;
                }
            }
            log.debug(string);
        };
    }

}
