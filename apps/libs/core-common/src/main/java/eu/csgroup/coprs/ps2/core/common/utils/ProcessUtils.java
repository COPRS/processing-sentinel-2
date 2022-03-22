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
