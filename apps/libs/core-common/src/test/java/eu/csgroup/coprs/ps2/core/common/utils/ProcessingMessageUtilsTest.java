/*
 * Copyright 2023 CS Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.csgroup.coprs.ps2.core.common.utils;

import eu.csgroup.coprs.ps2.core.common.exception.InvalidMessageException;
import eu.csgroup.coprs.ps2.core.common.model.processing.ProcessingMessage;
import eu.csgroup.coprs.ps2.core.common.settings.MessageParameters;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProcessingMessageUtilsTest {

    private static final String fieldName = "fieldName";
    private static final String fieldValue = "fieldValue";
    private static final String metadata = "metadata";
    private static final String metadataValue = "metadataValue";
    private static final Instant T0 = Instant.parse("2022-08-09T15:39:41.000000Z");

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

    @Test
    void getT0PdgsDate_metadata() {
        // Given
        final ProcessingMessage processingMessage = ProcessingMessageUtils.create()
                .setMetadata(Map.of(MessageParameters.T0_PDGS_DATE_FIELD, T0));
        // When
        final Instant t0PdgsDate = ProcessingMessageUtils.getT0PdgsDate(processingMessage);
        // Then
        assertEquals(T0, t0PdgsDate);
    }

    @Test
    void getT0PdgsDate_additionalFields() {
        // Given
        final ProcessingMessage processingMessage = ProcessingMessageUtils.create()
                .setAdditionalFields(Map.of(MessageParameters.T0_PDGS_DATE_FIELD, T0));
        // When
        final Instant t0PdgsDate = ProcessingMessageUtils.getT0PdgsDate(processingMessage);
        // Then
        assertEquals(T0, t0PdgsDate);
    }

    @Test
    void getT0PdgsDate_none() {
        // Given
        final ProcessingMessage processingMessage = withFields();
        // When
        final Instant t0PdgsDate = ProcessingMessageUtils.getT0PdgsDate(processingMessage);
        // Then
        assertEquals(Instant.EPOCH, t0PdgsDate);
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
