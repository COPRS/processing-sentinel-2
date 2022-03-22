package eu.csgroup.coprs.ps2.core.common.utils;

import eu.csgroup.coprs.ps2.core.common.AbstractSpringBootTest;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ScriptUtilsTest extends AbstractSpringBootTest {

    private static final String SCRIPT = "./script.sh";
    private static final String WORKDIR = "src/test/resources/scriptUtilsTest";

    @Override
    public void setup() {
        //
    }

    @Test
    void run() {
        final String workdir = Paths.get(WORKDIR).toAbsolutePath().toString();
        final Integer exitCode = ScriptUtils.run(workdir, SCRIPT);
        assertEquals(0, exitCode);
    }

}
