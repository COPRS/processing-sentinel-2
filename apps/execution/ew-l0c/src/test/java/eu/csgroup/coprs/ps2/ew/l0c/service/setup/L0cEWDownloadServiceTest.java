package eu.csgroup.coprs.ps2.ew.l0c.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.utils.FileOperationUtils;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class L0cEWDownloadServiceTest extends AbstractTest {

    @Mock
    private ObsService obsService;

    private L0cEWDownloadService l0cEWDownloadService;

    @Override
    public void setup() throws Exception {
        l0cEWDownloadService = new L0cEWDownloadService(obsService);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void download() {
        // Given
        final Set<FileInfo> fileInfoSet = Set.of(
                new FileInfo().setObsName("file1").setProductFamily(ProductFamily.S2_AUX).setAuxProductType(AuxProductType.AUX_UT1UTC).setFullLocalPath("/path/to/file1"),
                new FileInfo().setObsName("file2").setProductFamily(ProductFamily.S2_AUX).setAuxProductType(AuxProductType.GIP_ATMIMA).setFullLocalPath("/path/to/file2")
        );

        try (MockedStatic<FileOperationUtils> fileOperationUtilsMockedStatic = Mockito.mockStatic(FileOperationUtils.class)) {

            // When
            l0cEWDownloadService.download(fileInfoSet);

            // Then
            verify(obsService).download(fileInfoSet);
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.move(any(), any()), times(2));
            fileOperationUtilsMockedStatic.verify(() -> FileOperationUtils.deleteFolders(any()));
        }
    }

}
