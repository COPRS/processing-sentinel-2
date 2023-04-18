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
