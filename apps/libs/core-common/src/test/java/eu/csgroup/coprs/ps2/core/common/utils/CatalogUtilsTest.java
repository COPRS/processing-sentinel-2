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
import eu.csgroup.coprs.ps2.core.common.model.catalog.SessionCatalogData;
import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CatalogUtilsTest extends AbstractTest {

    private static final String PROPERTY = "property";
    private static final String PROPERTY_VALUE = "propertyValue";
    private static final String BAD_PROPERTY = "badProperty";

    private SessionCatalogData sessionCatalogData;

    @Override
    public void setup() throws Exception {
        sessionCatalogData = podamFactory.manufacturePojo(SessionCatalogData.class);
        sessionCatalogData.setAdditionalProperties(Map.of(PROPERTY, PROPERTY_VALUE));
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void hasAdditionalProperty() {
        // When
        final boolean hasAdditionalProperty = CatalogUtils.hasAdditionalProperty(sessionCatalogData, PROPERTY);
        // Then
        assertTrue(hasAdditionalProperty);
    }

    @Test
    void hasAdditionalProperty_not() {
        // When
        final boolean hasAdditionalProperty = CatalogUtils.hasAdditionalProperty(sessionCatalogData, BAD_PROPERTY);
        // Then
        assertFalse(hasAdditionalProperty);
    }

    @Test
    void getAdditionalProperty() {
        // When
        final String additionalProperty = CatalogUtils.getAdditionalProperty(sessionCatalogData, PROPERTY, String.class);
        // Then
        assertEquals(PROPERTY_VALUE, additionalProperty);
    }

    @Test
    void getAdditionalProperty_not() {
        // When Then
        assertThrows(InvalidMessageException.class, () -> CatalogUtils.getAdditionalProperty(sessionCatalogData, BAD_PROPERTY, String.class));
    }

    @Test
    void getToPdgsDate_1() {
        // Given
        final Instant now = Instant.now();
        final String toPdgsDateProperty = (String) ReflectionTestUtils.getField(CatalogUtils.class, "TO_PDGS_DATE_PROPERTY_1");
        assert toPdgsDateProperty != null;
        sessionCatalogData.setAdditionalProperties(Map.of(toPdgsDateProperty, now));
        // When
        final Instant t0PdgsDate = CatalogUtils.getT0PdgsDate(sessionCatalogData);
        // Then
        assertEquals(now, t0PdgsDate);
    }

    @Test
    void getToPdgsDate_2() {
        // Given
        final Instant now = Instant.now();
        final String toPdgsDateProperty = (String) ReflectionTestUtils.getField(CatalogUtils.class, "TO_PDGS_DATE_PROPERTY_2");
        assert toPdgsDateProperty != null;
        sessionCatalogData.setAdditionalProperties(Map.of(toPdgsDateProperty, now));
        // When
        final Instant t0PdgsDate = CatalogUtils.getT0PdgsDate(sessionCatalogData);
        // Then
        assertEquals(now, t0PdgsDate);
    }

}
