package eu.csgroup.coprs.ps2.ew.l0u.service.output;

import eu.csgroup.coprs.ps2.core.common.model.FileInfo;
import eu.csgroup.coprs.ps2.core.common.model.l0.L0uExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProductFamily;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class L0uEWMessageServiceTest extends AbstractTest {

    private L0uEWMessageService l0uEWMessageService;

    @Override
    public void setup() throws Exception {
        l0uEWMessageService = new L0uEWMessageService();
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void build() {

        // Given
        final L0uExecutionInput l0uExecutionInput = (L0uExecutionInput) new L0uExecutionInput()
                .setSession("session")
                .setSatellite("A")
                .setStation("foo")
                .setT0PdgsDate(Instant.EPOCH);
        Map<ProductFamily, Set<FileInfo>> fileInfosByFamily = Map.of(
                ProductFamily.S2_AUX, Collections.singleton(new FileInfo()),
                ProductFamily.S2_L0_DS, Collections.singleton(new FileInfo())
        );

        // When
        final Set<ProcessingMessage> messages = l0uEWMessageService.build(l0uExecutionInput, fileInfosByFamily, "outputFolder");

        // Then
        assertEquals(3, messages.size());
        final List<ProcessingMessage> catalogMessages = messages.stream().filter(processingMessage -> processingMessage.getProductFamily() != null).toList();
        final List<ProcessingMessage> executionMessages = messages.stream().filter(processingMessage -> processingMessage.getProductFamily() == null).toList();
        assertEquals(2, catalogMessages.size());
        assertTrue(catalogMessages.stream().allMatch(processingMessage -> processingMessage.getAdditionalFields().containsKey(MessageParameters.T0_PDGS_DATE_FIELD)));
        assertEquals(1, executionMessages.size());
        assertTrue(executionMessages.stream().allMatch(processingMessage -> processingMessage.getAdditionalFields().containsKey(MessageParameters.PREPARATION_INPUT_FIELD)));
    }

}
