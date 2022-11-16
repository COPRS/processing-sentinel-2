package eu.csgroup.coprs.ps2.ew.l0u.service.setup;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.obs.service.ObsService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

class L0uEWDownloadServiceTest extends AbstractTest {

    @Mock
    private ObsService obsService;

    @InjectMocks
    private L0uEWDownloadService l0uEWDownloadService;

    @Override
    public void setup() throws Exception {
        l0uEWDownloadService = new L0uEWDownloadService(obsService);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void download() {

        // Given
        final Set<FileInfo> fileInfoSet = Set.of(
                new FileInfo().setProductFamily(ProductFamily.EDRS_SESSION).setObsName("ch1_foo"),
                new FileInfo().setProductFamily(ProductFamily.EDRS_SESSION).setObsName("ch2_foo")
        );

        // When
        l0uEWDownloadService.download(fileInfoSet);

        // Then
        verify(obsService).download(fileInfoSet);
        assertTrue(fileInfoSet.stream().allMatch(fileInfo -> fileInfo.getLocalPath().contains("/ch_")));
    }

}
