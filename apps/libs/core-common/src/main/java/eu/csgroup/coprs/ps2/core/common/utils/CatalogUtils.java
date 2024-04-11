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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.csgroup.coprs.ps2.core.common.exception.InvalidMessageException;
import eu.csgroup.coprs.ps2.core.common.model.catalog.SessionCatalogData;

import java.time.Instant;
import java.util.Map;

public final class CatalogUtils {

    private static final String TO_PDGS_DATE_PROPERTY_1 = "t0_pdgs_date";
    private static final String TO_PDGS_DATE_PROPERTY_2 = "t0PdgsDate";

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static boolean hasAdditionalProperty(SessionCatalogData sessionCatalogData, String property) {
        final Map<String, Object> additionalProperties = sessionCatalogData.getAdditionalProperties();
        return additionalProperties.containsKey(property) && additionalProperties.get(property) != null;
    }

    public static <T> T getAdditionalProperty(SessionCatalogData sessionCatalogData, String property, Class<T> clazz) {

        if (!hasAdditionalProperty(sessionCatalogData, property)) {
            throw new InvalidMessageException("Catalog data does not contain property '" + property + "'");
        }

        return objectMapper.convertValue(sessionCatalogData.getAdditionalProperties().get(property), clazz);
    }

    public static Instant getT0PdgsDate(SessionCatalogData sessionCatalogData) {
        // Looking for t0PdgsDate with both known property names
        Instant t0PdgsDate = Instant.EPOCH;
        if (hasAdditionalProperty(sessionCatalogData, TO_PDGS_DATE_PROPERTY_1)) {
            t0PdgsDate = getAdditionalProperty(sessionCatalogData, TO_PDGS_DATE_PROPERTY_1, Instant.class);
        } else if (hasAdditionalProperty(sessionCatalogData, TO_PDGS_DATE_PROPERTY_2)) {
            t0PdgsDate = getAdditionalProperty(sessionCatalogData, TO_PDGS_DATE_PROPERTY_2, Instant.class);
        }
        return t0PdgsDate;
    }

    private CatalogUtils() {
    }

}
