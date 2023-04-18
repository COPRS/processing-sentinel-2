package eu.csgroup.coprs.ps2.pw.l1s.service.prepare;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class L1sPWExecutionInputServiceTest extends AbstractTest {

    @Mock
    private SharedProperties sharedProperties;
    @Mock
    private L1sAuxService auxService;
    @Mock
    private ObsBucketProperties bucketProperties;

    @InjectMocks
    private L1sPWExecutionInputService executionInputService;

    @Override
    public void setup() throws Exception {
        executionInputService = new L1sPWExecutionInputService(sharedProperties, auxService, bucketProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void create() {

        // Given
        when(auxService.getAux(any())).thenReturn(Map.of(AuxProductType.GIP_CLOINV, List.of(new FileInfo())));
        when(sharedProperties.getSharedFolderRoot()).thenReturn("/shared");
        when(sharedProperties.getMinGrRequired()).thenReturn(1L);

        // When
        final List<L1ExecutionInput> executionInputs = executionInputService.create(List.of(TestHelper.DATASTRIP, TestHelper.UPDATED_DATASTRIP));

        // Then
        verify(auxService, times(2)).getAux(any());
        assertEquals(2, executionInputs.size());
    }

}
