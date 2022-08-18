package eu.csgroup.coprs.ps2.core.common.utils;

import eu.csgroup.coprs.ps2.core.common.test.AbstractSpringBootTest;
import eu.csgroup.coprs.ps2.core.common.model.script.ScriptWrapper;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ScriptUtilsTest extends AbstractSpringBootTest {

    private static final String SCRIPT = "./script.sh";
    private static final String SCRIPT_EXIT = "./script_exit.sh";
    private static final String WORKDIR = "src/test/resources/scriptUtilsTest";
    private static final String WORKDIR_PATH = Paths.get(WORKDIR).toAbsolutePath().toString();


    @Override
    public void setup() {
        // Nothing
    }

    @Override
    public void teardown() throws Exception {
        // Nothing
    }

    @Test
    void run() {
        final Integer exitCode = ScriptUtils.run(new ScriptWrapper().setRunId("id").setWorkdir(WORKDIR_PATH).setCommand(List.of(SCRIPT)));
        assertEquals(0, exitCode);
    }

    @Test
    void run_parallel() {
        final Map<String, Integer> exitCodes = ScriptUtils.run(Set.of(
                        new ScriptWrapper().setRunId("id0").setWorkdir(WORKDIR_PATH).setCommand(List.of(SCRIPT)),
                        new ScriptWrapper().setRunId("id1").setWorkdir(WORKDIR_PATH).setCommand(List.of(SCRIPT_EXIT))
                )
        );
        assertEquals(0, exitCodes.get("id0"));
        assertEquals(3, exitCodes.get("id1"));
    }

}
