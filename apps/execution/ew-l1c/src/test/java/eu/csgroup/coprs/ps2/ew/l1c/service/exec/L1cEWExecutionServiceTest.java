package eu.csgroup.coprs.ps2.ew.l1c.service.exec;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.DatatakeType;
import eu.csgroup.coprs.ps2.core.common.model.script.ScriptWrapper;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.ScriptUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class L1cEWExecutionServiceTest extends AbstractTest {

    @Mock
    private SharedProperties sharedProperties;

    @InjectMocks
    private L1cEWExecutionService executionService;

    @Override
    public void setup() throws Exception {
        executionService = new L1cEWExecutionService(sharedProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void processing() {

        // Given
        final L1ExecutionInput executionInput =
                (L1ExecutionInput) new L1ExecutionInput().setDatatakeType(DatatakeType.RAW).setAuxFolder("foo").setInputFolder("foo").setOutputFolder("foo");
        when(sharedProperties.getMaxParallelTasks()).thenReturn(1);
        when(sharedProperties.getDemFolderRoot()).thenReturn("foo");
        when(sharedProperties.getGridFolderRoot()).thenReturn("bar");

        try (MockedStatic<ScriptUtils> scriptUtilsMockedStatic = Mockito.mockStatic(ScriptUtils.class)) {

            // When
            executionService.execute(executionInput, UUID.randomUUID());

            // Then
            scriptUtilsMockedStatic.verify(() -> ScriptUtils.run(any(ScriptWrapper.class)), times(1));
        }
    }

    @Test
    void getLevel() {
        assertEquals("L1c", executionService.getLevel());
    }

}
