package eu.csgroup.coprs.ps2.core.common.utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProcessUtilsTest {

    private static final Path script = Paths.get("src/test/resources/processUtilsTest/wait.sh").toAbsolutePath();

    @Test
    void kill() throws IOException, InterruptedException {

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(script.toString());
        final Process process = processBuilder.start();


        ProcessUtils.kill(script.getFileName().toString());

        final int exitCode = process.waitFor();

        assertEquals(137, exitCode);
    }

}
