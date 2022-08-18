package eu.csgroup.coprs.ps2.core.common.utils;

import eu.csgroup.coprs.ps2.core.common.exception.InvalidMessageException;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProcessingMessageUtilsTest {

    private static final String fieldName = "fieldName";
    private static final String fieldValue = "fieldValue";
    private static final String metadata = "metadata";
    private static final String metadataValue = "metadataValue";

    @Test
    void create() {
        final ProcessingMessage processingMessage = ProcessingMessageUtils.create();
        assertNotNull(processingMessage);
    }

    @Test
    void hasAdditionalField() {
        // When
        final boolean hasAdditionalField = ProcessingMessageUtils.hasAdditionalField(withFields(), fieldName);
        // Then
        assertTrue(hasAdditionalField);
    }

    @Test
    void hasAdditionalField_not() {
        // When
        final boolean hasAdditionalField = ProcessingMessageUtils.hasAdditionalField(withoutFields(), fieldName);
        // Then
        assertFalse(hasAdditionalField);
    }

    @Test
    void getAdditionalField() {
        // When
        final String additionalField = ProcessingMessageUtils.getAdditionalField(withFields(), fieldName, String.class);
        // Then
        assertEquals(fieldValue, additionalField);
    }

    @Test
    void getAdditionalField_not() {
        // Given
        final ProcessingMessage message = withoutFields();
        // When Then
        assertThrows(InvalidMessageException.class, () -> ProcessingMessageUtils.getAdditionalField(message, fieldName, String.class));
    }

    @Test
    void hasMetadata() {
        // When
        final boolean hasMetadata = ProcessingMessageUtils.hasMetadata(withFields(), metadata);
        // Then
        assertTrue(hasMetadata);
    }

    @Test
    void hasMetadata_not() {
        // When
        final boolean hasMetadata = ProcessingMessageUtils.hasMetadata(withoutFields(), metadata);
        // Then
        assertFalse(hasMetadata);
    }

    @Test
    void getMetadata() {
        // When
        final String metadata1 = ProcessingMessageUtils.getMetadata(withFields(), metadata, String.class);
        // Then
        assertEquals(metadataValue, metadata1);
    }

    @Test
    void getMetadata_not() {
        // Given
        final ProcessingMessage message = withoutFields();
        // When Then
        assertThrows(InvalidMessageException.class, () -> ProcessingMessageUtils.getMetadata(message, metadata, String.class));
    }

    private ProcessingMessage withFields() {
        return ProcessingMessageUtils.create()
                .setAdditionalFields(Map.of(fieldName, fieldValue))
                .setMetadata(Map.of(metadata, metadataValue));
    }

    private ProcessingMessage withoutFields() {
        return ProcessingMessageUtils.create();
    }

}
