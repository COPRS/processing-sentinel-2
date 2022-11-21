package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.catalog.AuxCatalogData;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
import eu.csgroup.coprs.ps2.pw.l0c.model.L0cAuxFile;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class L0cAuxServiceTest extends AbstractTest {

    @Mock
    private CatalogService catalogService;
    @Mock
    private ObsBucketProperties bucketProperties;

    @InjectMocks
    private L0cAuxService auxService;

    @Override
    public void setup() throws Exception {
        auxService = new L0cAuxService(catalogService, bucketProperties);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void getAux() {

        // Given
        final AuxCatalogData auxCatalogData = (AuxCatalogData) new AuxCatalogData().setProductName("foo").setKeyObjectStorage("bar");
        when(catalogService.retrieveLatestAuxData(any(), any(), any(), any(), any())).thenReturn(Optional.of(auxCatalogData));

        // When
        final Map<L0cAuxFile, List<FileInfo>> filesByAux = auxService.getAux(TestHelper.DATASTRIP);

        // Then
        assertEquals(14, filesByAux.size());
        assertTrue(filesByAux.values().stream().flatMap(List::stream).allMatch(fileInfo -> fileInfo.getKey().equals("bar")));
        assertTrue(filesByAux.values().stream().flatMap(List::stream).allMatch(fileInfo -> fileInfo.getLocalName().equals("foo")));
    }

}
