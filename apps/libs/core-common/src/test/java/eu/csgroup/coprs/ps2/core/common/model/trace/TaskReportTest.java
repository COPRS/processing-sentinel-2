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

package eu.csgroup.coprs.ps2.core.common.model.trace;

import eu.csgroup.coprs.ps2.core.common.test.AbstractSpringBootTest;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TaskReportTest extends AbstractSpringBootTest {

    private static final String satellite = "S2A";

    private UUID parentUid;
    private UUID predecessorUid;


    @Override
    public void setup() {
        parentUid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        predecessorUid = UUID.fromString("11111111-1111-1111-1111-111111111111");
    }

    @Override
    public void teardown() throws Exception {
        // Nothing
    }

    @Test
    void basic_begin_end_traces() {

        try (LogCaptor logCaptor = LogCaptor.forClass(TraceLogger.class)) {

            final TaskReport taskReport = basicTraceManager("basicTask");

            taskReport.begin("begin message");
            taskReport.end("end message");

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

            final TaskReport taskReport = fullTraceManager("chainTask");

            taskReport.begin("begin message");
            taskReport.end("end message");

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

            final TaskReport taskReport = fullTraceManager("chainTask");

            taskReport.begin("begin message");
            taskReport.warning("warning message");

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

            final TaskReport taskReport = fullTraceManager("chainTask");

            taskReport.begin("begin message");
            taskReport.error("error message");

            assertEquals(2, logCaptor.getLogs().size());

            final String beginLog = logCaptor.getLogs().get(0);
            final String endLog = logCaptor.getLogs().get(1);

            assertTrue(beginLog.contains("BEGIN"));
            assertTrue(endLog.contains("END"));
            assertTrue(endLog.contains("ERROR"));
            assertTrue(endLog.contains("\"status\":\"NOK\""));
        }
    }

    private TaskReport basicTraceManager(String taskName) {
        return traceManager(taskName, satellite, null, null);
    }

    private TaskReport fullTraceManager(String taskName) {
        return traceManager(taskName, satellite, parentUid, predecessorUid);
    }

    private TaskReport traceManager(String taskName, String satellite, UUID parentUid, UUID predecessorUid) {
        return new TaskReport()
                .setTaskName(taskName)
                .setSatellite(satellite)
                .setParentUid(parentUid)
                .setPredecessorUid(predecessorUid);
    }

}
