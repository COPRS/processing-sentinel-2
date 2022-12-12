package eu.csgroup.coprs.ps2.ew.l1sa.service.output;

import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

class L1saEWMessageServiceTest extends AbstractTest {

    private L1saEWMessageService messageService;

    @Override
    public void setup() throws Exception {
        messageService = new L1saEWMessageService();
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void getCustomOutputs() {

        // Given
        try (MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class)) {
            fileOperationUtilsMockedStatic.when(() -> FileOperationUtils.findFoldersInTree(any(), any())).thenReturn(List.of(Path.of("path1"), Path.of("path2")));

            // When
            final Set<String> customOutputs = messageService.getCustomOutputs("folder");

            // Then
            assertEquals(2, customOutputs.size());
        }
    }

}
