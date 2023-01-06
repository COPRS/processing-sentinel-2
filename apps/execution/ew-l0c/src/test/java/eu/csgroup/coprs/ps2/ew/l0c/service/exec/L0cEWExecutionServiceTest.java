package eu.csgroup.coprs.ps2.ew.l0c.service.exec;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0cExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.script.ScriptWrapper;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.FileContentUtils;
import eu.csgroup.coprs.ps2.core.common.utils.ScriptUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class L0cEWExecutionServiceTest extends AbstractTest {

    @Mock
    private SharedProperties sharedProperties;

    @InjectMocks
    private L0cEWExecutionService executionService;

    @Override
    public void setup() throws Exception {
        executionService = new L0cEWExecutionService(sharedProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void processing() {

        // Given
        final L0cExecutionInput executionInput = (L0cExecutionInput) new L0cExecutionInput().setAuxFolder("foo").setInputFolder("foo").setOutputFolder("foo");
        when(sharedProperties.getMaxParallelTasks()).thenReturn(1);
        when(sharedProperties.getDemFolderRoot()).thenReturn("foo");
        when(sharedProperties.getGridFolderRoot()).thenReturn("bar");

        try (
                MockedStatic<ScriptUtils> scriptUtilsMockedStatic = Mockito.mockStatic(ScriptUtils.class);
                MockedStatic<FileContentUtils> fileContentUtilsMockedStatic = Mockito.mockStatic(FileContentUtils.class);
        ) {
            fileContentUtilsMockedStatic.when(() -> FileContentUtils.grepAll(any(), any())).thenReturn(Collections.emptyList());

            // When
            executionService.execute(executionInput, UUID.randomUUID());

            // Then
            scriptUtilsMockedStatic.verify(() -> ScriptUtils.run(any(ScriptWrapper.class)), times(3));
        }
    }

    @Test
    void getLevel() {
        assertEquals("L0c", executionService.getLevel());
    }

}
