package eu.csgroup.coprs.ps2.core.common.utils;


import eu.csgroup.coprs.ps2.core.common.exception.ScriptExecutionException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class ScriptUtils {

    public static Integer run(String workdir, String... command) {
        return run(workdir, Collections.emptyMap(), null, null, command);
    }

    public static Integer run(String workdir, Map<String, String> environment, Long timeOut, TimeUnit timeUnit, String... command) {

        log.info("Running command {} in workdir {}", command, workdir);

        Process process;
        ProcessBuilder processBuilder = new ProcessBuilder();
        int exitCode;

        processBuilder.command(command);
        processBuilder.directory(new File(workdir));
        processBuilder.environment().putAll(environment);
        processBuilder.redirectErrorStream(true);

        try {
            process = processBuilder.start();
        } catch (Exception e) {
            throw new ScriptExecutionException("Unable to create script process", e);
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        try {

            executorService.submit(new ScriptLogger(process.getInputStream()));

            if (timeOut == null) {
                exitCode = process.waitFor();
            } else {
                process.waitFor(timeOut, timeUnit);
                exitCode = process.exitValue();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ScriptExecutionException("Script execution interrupted", e);
        } catch (Exception e) {
            throw new ScriptExecutionException("Script execution failed", e);
        } finally {
            executorService.shutdown();
        }

        log.info("Finished Running command {} in workdir {}", command, workdir);

        return exitCode;
    }

    private ScriptUtils() {
    }

}
