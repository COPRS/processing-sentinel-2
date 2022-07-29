package eu.csgroup.coprs.ps2.core.common.service.trace;

import eu.csgroup.coprs.ps2.core.common.AbstractSpringBootTest;
import eu.csgroup.coprs.ps2.core.common.model.trace.TraceLogger;
import eu.csgroup.coprs.ps2.core.common.model.trace.TraceManager;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TraceManagerTest extends AbstractSpringBootTest {

    private static final String satellite = "S2A";

    private UUID parentUid;
    private UUID predecessorUid;


    @Override
    public void setup() {
        parentUid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        predecessorUid = UUID.fromString("11111111-1111-1111-1111-111111111111");
    }

    @Test
    void basic_begin_end_traces() {

        try (LogCaptor logCaptor = LogCaptor.forClass(TraceLogger.class)) {

            final TraceManager traceManager = basicTraceManager("basicTask");

            traceManager.begin("begin message", null);
            traceManager.end("end message", null, null);

            assertEquals(2, logCaptor.getLogs().size());

            final String beginLog = logCaptor.getLogs().get(0);
            final String endLog = logCaptor.getLogs().get(1);

            assertTrue(beginLog.contains("BEGIN"));
            assertTrue(endLog.contains("END"));
            assertTrue(endLog.contains("duration"));
        }
    }

    @Test
    void chain_begin_end_traces() {

        try (LogCaptor logCaptor = LogCaptor.forClass(TraceLogger.class)) {

            final TraceManager traceManager = fullTraceManager("chainTask");

            traceManager.begin("begin message", null);
            traceManager.end("end message", null, null);

            assertEquals(2, logCaptor.getLogs().size());

            final String beginLog = logCaptor.getLogs().get(0);
            final String endLog = logCaptor.getLogs().get(1);

            assertTrue(beginLog.contains("BEGIN"));
            assertTrue(beginLog.contains(parentUid.toString()));
            assertTrue(beginLog.contains(predecessorUid.toString()));
            assertTrue(endLog.contains("END"));
        }
    }

    @Test
    void warning() {

        try (LogCaptor logCaptor = LogCaptor.forClass(TraceLogger.class)) {

            final TraceManager traceManager = fullTraceManager("chainTask");

            traceManager.begin("begin message", null);
            traceManager.warning("warning message", null, null);

            assertEquals(2, logCaptor.getLogs().size());

            final String beginLog = logCaptor.getLogs().get(0);
            final String endLog = logCaptor.getLogs().get(1);

            assertTrue(beginLog.contains("BEGIN"));
            assertTrue(endLog.contains("END"));
            assertTrue(endLog.contains("WARNING"));
            assertTrue(endLog.contains("\"status\":\"OK\""));
        }
    }

    @Test
    void error() {

        try (LogCaptor logCaptor = LogCaptor.forClass(TraceLogger.class)) {

            final TraceManager traceManager = fullTraceManager("chainTask");

            traceManager.begin("begin message", null);
            traceManager.error("error message");

            assertEquals(2, logCaptor.getLogs().size());

            final String beginLog = logCaptor.getLogs().get(0);
            final String endLog = logCaptor.getLogs().get(1);

            assertTrue(beginLog.contains("BEGIN"));
            assertTrue(endLog.contains("END"));
            assertTrue(endLog.contains("ERROR"));
            assertTrue(endLog.contains("\"status\":\"NOK\""));
        }
    }

    private TraceManager basicTraceManager(String taskName) {
        return traceManager(taskName, satellite, null, null);
    }

    private TraceManager fullTraceManager(String taskName) {
        return traceManager(taskName, satellite, parentUid, predecessorUid);
    }

    private TraceManager traceManager(String taskName, String satellite, UUID parentUid, UUID predecessorUid) {
        return new TraceManager()
                .setTaskName(taskName)
                .setSatellite(satellite)
                .setParentUid(parentUid)
                .setPredecessorUid(predecessorUid);
    }

}