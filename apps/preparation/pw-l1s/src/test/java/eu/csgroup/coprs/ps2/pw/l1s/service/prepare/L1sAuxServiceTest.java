package eu.csgroup.coprs.ps2.pw.l1s.service.prepare;

import eu.csgroup.coprs.ps2.core.common.config.SharedProperties;
import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.model.catalog.AuxCatalogData;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.obs.config.ObsBucketProperties;
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

class L1sAuxServiceTest extends AbstractTest {

    @Mock
    private CatalogService catalogService;
    @Mock
    private ObsBucketProperties bucketProperties;
    @Mock
    private SharedProperties sharedProperties;

    @InjectMocks
    private L1sAuxService auxService;

    @Override
    public void setup() throws Exception {
        auxService = new L1sAuxService(catalogService, bucketProperties, sharedProperties);
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
        when(sharedProperties.getSharedFolderRoot()).thenReturn("/shared");

        // When
        final Map<AuxProductType, List<FileInfo>> filesByAux = auxService.getAux(TestHelper.UPDATED_DATASTRIP);

        // Then
        assertEquals(42, filesByAux.size());
        assertTrue(filesByAux.values().stream().flatMap(List::stream).allMatch(fileInfo -> fileInfo.getKey().equals("bar")));
        assertTrue(filesByAux.values().stream().flatMap(List::stream).allMatch(fileInfo -> fileInfo.getLocalName().equals("foo")));
    }

}
