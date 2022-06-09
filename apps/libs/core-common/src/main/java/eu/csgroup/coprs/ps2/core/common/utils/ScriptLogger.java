package eu.csgroup.coprs.ps2.core.common.utils;

import eu.csgroup.coprs.ps2.core.common.exception.ScriptExecutionException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

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

    private static final List<String> INFO_LEVEL_MARKERS = List.of("[W]", "[E]", "[WARNING]", "[ERROR]");

    private InputStream standardStream;

    @Override
    public void run() {
        try (BufferedReader standardBufferReader = new BufferedReader(new InputStreamReader(standardStream))) {
            standardBufferReader.lines().forEach(logger());
        } catch (Exception e) {
            throw new ScriptExecutionException("Unable to process script logs", e);
        }
    }

    private Consumer<String> logger() {
        return string -> {
            for (String marker : INFO_LEVEL_MARKERS) {
                if (string.contains(marker)) {
                    log.info(string);
                    return;
                }
            }
            log.debug(string);
        };
    }

}
