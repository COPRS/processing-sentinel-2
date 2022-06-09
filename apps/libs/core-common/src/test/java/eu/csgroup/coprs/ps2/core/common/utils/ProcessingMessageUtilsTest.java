package eu.csgroup.coprs.ps2.core.common.utils;

import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProcessingMessageUtilsTest {

    @Test
    void create() {
        final ProcessingMessage processingMessage = ProcessingMessageUtils.create();
        assertNotNull(processingMessage);
    }

}
