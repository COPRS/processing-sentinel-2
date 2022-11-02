package eu.csgroup.coprs.ps2.core.ew.service;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.ew.model.helper.Input;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EWMessageServiceTest extends AbstractTest {

    private EWMessageService<Input> messageService;

    @SuppressWarnings("unchecked")
    @Override
    public void setup() throws Exception {
        messageService = Mockito.mock(EWMessageService.class, Mockito.CALLS_REAL_METHODS);
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void buildCatalogMessages() {

        // Given
        Map<ProductFamily, Set<FileInfo>> fileInfosByFamily = new HashMap<>();
        fileInfosByFamily.put(
                ProductFamily.S2_AUX,
                Set.of(
                        new FileInfo().setObsURL("s3://bucket/path/aux1"),
                        new FileInfo().setObsURL("s3://bucket/path/aux2")
                ));
        fileInfosByFamily.put(
                ProductFamily.S2_L0_DS,
                Set.of(
                        new FileInfo().setObsURL("s3://bucket/path/ds1"),
                        new FileInfo().setObsURL("s3://bucket/path/ds2"),
                        new FileInfo().setObsURL("s3://bucket/path/ds3")
                ));

        final Input input = podamFactory.manufacturePojo(Input.class);

        // When
        final Set<ProcessingMessage> processingMessages = messageService.buildCatalogMessages(fileInfosByFamily, input);

        // Then
        assertEquals(5, processingMessages.size());
        assertEquals(2,
                processingMessages.stream().filter(processingMessage -> ProductFamily.S2_AUX.equals(processingMessage.getProductFamily())).count()
        );
        assertEquals(3,
                processingMessages.stream().filter(processingMessage -> ProductFamily.S2_L0_DS.equals(processingMessage.getProductFamily())).count()
        );
    }

}
