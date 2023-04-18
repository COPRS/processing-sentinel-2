package eu.csgroup.coprs.ps2.pw.l1c.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.csgroup.coprs.ps2.core.common.model.l1.L1ExecutionInput;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class L1cPWMessageServiceTest extends AbstractTest {

    private L1cPWMessageService messageService;

    @Override
    public void setup() throws Exception {
        messageService = new L1cPWMessageService(new ObjectMapper());
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void build() {
        // When
        final Set<ProcessingMessage> messages = messageService.build(new L1ExecutionInput(), Set.of("tile1", "tile2"));
        // Then
        assertEquals(3, messages.size());
    }

}
