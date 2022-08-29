package eu.csgroup.coprs.ps2.core.common.utils;

import eu.csgroup.coprs.ps2.core.common.test.AbstractTest;
import eu.csgroup.coprs.ps2.core.common.exception.InvalidMessageException;
import eu.csgroup.coprs.ps2.core.common.model.catalog.SessionCatalogData;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CatalogUtilsTest extends AbstractTest {

    public static final String property = "property";
    public static final String propertyValue = "propertyValue";
    public static final String badProperty = "badProperty";

    private SessionCatalogData sessionCatalogData;

    @Override
    public void setup() throws Exception {
        sessionCatalogData = podamFactory.manufacturePojo(SessionCatalogData.class);
        sessionCatalogData.setAdditionalProperties(Map.of(property, propertyValue));
    }

    @Override
    public void teardown() throws Exception {
        //
    }

    @Test
    void hasAdditionalProperty() {
        // When
        final boolean hasAdditionalProperty = CatalogUtils.hasAdditionalProperty(sessionCatalogData, property);
        // Then
        assertTrue(hasAdditionalProperty);
    }

    @Test
    void hasAdditionalProperty_not() {
        // When
        final boolean hasAdditionalProperty = CatalogUtils.hasAdditionalProperty(sessionCatalogData, badProperty);
        // Then
        assertFalse(hasAdditionalProperty);
    }

    @Test
    void getAdditionalProperty() {
        // When
        final String additionalProperty = CatalogUtils.getAdditionalProperty(sessionCatalogData, property, String.class);
        // Then
        assertEquals(propertyValue, additionalProperty);
    }

    @Test
    void getAdditionalProperty_not() {
        // When Then
        assertThrows(InvalidMessageException.class, () -> CatalogUtils.getAdditionalProperty(sessionCatalogData, badProperty, String.class));
    }

}
