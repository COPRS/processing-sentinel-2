package eu.csgroup.coprs.ps2.pw.l0c.service.prepare;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.catalog.AuxCatalogData;
import eu.csgroup.coprs.ps2.core.common.model.catalog.BaseCatalogData;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.pw.l0c.config.L0cPreparationProperties;
import eu.csgroup.coprs.ps2.pw.l0c.model.AuxFile;
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

class AuxServiceTest extends AbstractTest {

    @Mock
    private CatalogService catalogService;
    @Mock
    private L0cPreparationProperties l0cPreparationProperties;

    @InjectMocks
    private AuxService auxService;

    @Override
    public void setup() throws Exception {
        auxService = new AuxService(catalogService, l0cPreparationProperties);
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
        final Map<AuxFile, List<FileInfo>> filesByAux = auxService.getAux(TestHelper.DATASTRIP);

        // Then
        assertEquals(14, filesByAux.size());
        assertTrue(filesByAux.values().stream().flatMap(List::stream).allMatch(fileInfo -> fileInfo.getKey().equals("bar")));
        assertTrue(filesByAux.values().stream().flatMap(List::stream).allMatch(fileInfo -> fileInfo.getLocalName().equals("foo")));
    }

}
