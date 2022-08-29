package eu.csgroup.coprs.ps2.core.common.service.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.csgroup.coprs.ps2.core.common.test.AbstractSpringBootTest;
import eu.csgroup.coprs.ps2.core.common.exception.CatalogQueryException;
import eu.csgroup.coprs.ps2.core.common.model.aux.AuxProductType;
import eu.csgroup.coprs.ps2.core.common.model.catalog.AuxCatalogData;
import eu.csgroup.coprs.ps2.core.common.model.catalog.SessionCatalogData;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CatalogServiceTest extends AbstractSpringBootTest {

    private static final Path auxPath = Paths.get("src/test/resources/catalogTest/aux2.json");
    private static final Path sessionPath = Paths.get("src/test/resources/catalogTest/session2.json");
    private static final Instant from = Instant.now().minus(60, ChronoUnit.DAYS);
    private static final Instant to = Instant.now().minus(30, ChronoUnit.DAYS);
    private static final String sessionName = "session";

    public static MockWebServer mockWebServer;

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private ObjectMapper objectMapper;

    private AuxCatalogData[] auxCatalogData;
    private SessionCatalogData[] sessionCatalogData;

    @Override
    public void setup() throws Exception {

        mockWebServer = new MockWebServer();
        mockWebServer.start(6969);

        auxCatalogData = objectMapper.readValue(auxPath.toFile(), AuxCatalogData[].class);
        sessionCatalogData = objectMapper.readValue(sessionPath.toFile(), SessionCatalogData[].class);
    }

    @Override
    public void teardown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void retrieveLatestAuxData() throws IOException {
        // Given
        mockBasicAuxRequest();
        // When
        final Optional<AuxCatalogData> data = catalogService.retrieveLatestAuxData(AuxProductType.GIP_ATMSAD, "B", from, to);
        // Then
        assertTrue(data.isPresent());
        assertEquals(auxCatalogData[0].getProductName(), data.get().getProductName());
    }

    @Test
    void retrieveLatestAuxData_withBand() throws IOException {
        // Given
        mockBasicAuxRequest();
        // When
        final Optional<AuxCatalogData> data = catalogService.retrieveLatestAuxData(AuxProductType.GIP_ATMSAD, "B", from, to, "B01");
        // Then
        assertTrue(data.isPresent());
        assertEquals(auxCatalogData[0].getProductName(), data.get().getProductName());
    }

    @Test
    void retrieveSessionData() throws IOException {
        // Given
        mockBasicSessionRequest();
        // When
        final List<SessionCatalogData> data = catalogService.retrieveSessionData(sessionName);
        // Then
        assertFalse(data.isEmpty());
        assertEquals(168, data.size());
    }

    @Test
    void retrieve_with_4xxError() {
        // Given
        mock4xxError();
        // WhenThen
        assertThrows(CatalogQueryException.class, () -> catalogService.retrieveLatestAuxData(AuxProductType.GIP_ATMSAD, "B", from, to));
    }

    @Test
    void retrieve_with_5xxError() {
        // Given
        mock5xxError();
        // WhenThen
        assertThrows(CatalogQueryException.class, () -> catalogService.retrieveSessionData(sessionName));
    }

    private void mockBasicAuxRequest() throws IOException {
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(objectMapper.writeValueAsString(auxCatalogData))
                        .addHeader("Content-Type", "application/json")
        );
    }

    private void mockBasicSessionRequest() throws IOException {
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(objectMapper.writeValueAsString(sessionCatalogData))
                        .addHeader("Content-Type", "application/json")
        );
    }

    private void mock4xxError() {
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(HttpStatus.NOT_FOUND.value())
                        .setHeader("content-type", "application/json")
                        .setBody("{}"));
    }

    private void mock5xxError() {
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .setHeader("content-type", "application/json")
                        .setBody("{}"));
    }

}