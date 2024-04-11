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

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ProcessUtils {

    public static void kill(String command) {

        log.info("Killing process: {}", command);

        try {
            ProcessHandle.allProcesses()
                    .filter(handle -> handle.info().commandLine().stream().anyMatch(s -> s.contains(command)))
                    .findAny()
                    .ifPresent(processHandle -> {
                        boolean result = processHandle.destroyForcibly();
                        if (result) {
                            log.info("Successfully killed command: {}", command);
                        } else {
                            log.warn("Unable to kill command: {}", command);
                        }
                    });
        } catch (Exception e) {
            log.warn("Unable to kill command: {}", command);
        }
    }

    private ProcessUtils() {
    }

}
