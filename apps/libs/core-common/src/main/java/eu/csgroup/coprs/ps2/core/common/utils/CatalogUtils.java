package eu.csgroup.coprs.ps2.core.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.csgroup.coprs.ps2.core.common.exception.InvalidMessageException;
import eu.csgroup.coprs.ps2.core.common.model.catalog.SessionCatalogData;

import java.util.Map;

public final class CatalogUtils {

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

    private CatalogUtils() {
    }

}
