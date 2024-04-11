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
import eu.csgroup.coprs.ps2.core.common.model.script.ScriptWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
public final class ScriptUtils {

    private static final String SCRIPT_EXECUTION_INTERRUPTED = "Script execution interrupted";
    private static final String SCRIPT_EXECUTION_FAILED = "Script execution failed";

    public static Integer run(ScriptWrapper scriptWrapper) {
        try {
            return doRun(scriptWrapper)
                    .get()
                    .entrySet()
                    .stream()
                    .findAny()
                    .orElseThrow(() -> new ScriptExecutionException(SCRIPT_EXECUTION_FAILED))
                    .getValue();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ScriptExecutionException(SCRIPT_EXECUTION_INTERRUPTED, e);
        } catch (Exception e) {
            throw new ScriptExecutionException(SCRIPT_EXECUTION_FAILED, e);
        }
    }

    public static Map<String, Integer> run(Set<ScriptWrapper> scriptWrapperSet) {

        final List<CompletableFuture<Map<String, Integer>>> futures = scriptWrapperSet
                .stream()
                .map(ScriptUtils::doRun)
                .toList();

        try {

            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(
                            unused -> futures.stream()
                                    .map(CompletableFuture::join)
                                    .toList())
                    .get()
                    .stream()
                    .flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ScriptExecutionException(SCRIPT_EXECUTION_INTERRUPTED, e);
        } catch (Exception e) {
            throw new ScriptExecutionException(SCRIPT_EXECUTION_FAILED, e);
        }
    }

    private static CompletableFuture<Map<String, Integer>> doRun(ScriptWrapper scriptWrapper) {

        return CompletableFuture.supplyAsync(() -> {

                    final String[] command = scriptWrapper.getCommandArgs();
                    final String workdir = scriptWrapper.getWorkdir();

                    log.info("Running command {} in workdir {}", command, workdir);

                    Process process;
                    ProcessBuilder processBuilder = new ProcessBuilder();
                    int exitCode;

                    processBuilder.command(command);
                    processBuilder.directory(new File(workdir));
                    processBuilder.environment().putAll(scriptWrapper.getEnvironment());
                    processBuilder.redirectErrorStream(true);

                    try {
                        process = processBuilder.start();
                    } catch (Exception e) {
                        throw new ScriptExecutionException("Unable to create script process", e);
                    }

                    ExecutorService executorService = Executors.newSingleThreadExecutor();

                    try {

                        executorService.submit(new ScriptLogger(process.getInputStream(), scriptWrapper.getLogWhitelist()));

                        if (scriptWrapper.getTimeOut() == null) {
                            exitCode = process.waitFor();
                        } else {
                            process.waitFor(scriptWrapper.getTimeOut(), scriptWrapper.getTimeUnit());
                            exitCode = process.exitValue();
                        }

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new ScriptExecutionException(SCRIPT_EXECUTION_INTERRUPTED, e);
                    } catch (Exception e) {
                        throw new ScriptExecutionException(SCRIPT_EXECUTION_FAILED, e);
                    } finally {
                        executorService.shutdown();
                    }

                    log.info("Finished Running command {} in workdir {}", command, workdir);

                    return Map.of(scriptWrapper.getRunId(), exitCode);
                }
        );
    }

    private ScriptUtils() {
    }

}
