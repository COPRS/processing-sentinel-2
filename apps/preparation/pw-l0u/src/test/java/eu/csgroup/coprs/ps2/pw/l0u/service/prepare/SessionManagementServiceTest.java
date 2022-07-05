package eu.csgroup.coprs.ps2.pw.l0u.service.prepare;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.csgroup.coprs.ps2.core.common.model.catalog.SessionCatalogData;
import eu.csgroup.coprs.ps2.core.common.service.catalog.CatalogService;
import eu.csgroup.coprs.ps2.pw.l0u.AbstractTest;
import eu.csgroup.coprs.ps2.pw.l0u.config.L0uPreparationProperties;
import eu.csgroup.coprs.ps2.pw.l0u.model.Session;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionManagementServiceTest extends AbstractTest {

    private static final Path sessionPath = Paths.get("src/test/resources/mdc_session.json");
    private static final String sessionName = "sessionName";

    @Mock
    private SessionService sessionService;

    @Mock
    private CatalogService catalogService;

    @Mock
    private L0uPreparationProperties l0uPreparationProperties;

    @InjectMocks
    private SessionManagementService sessionManagementService;

    private ObjectMapper objectMapper;
    private List<SessionCatalogData> sessionCatalogDataList;
    private Session session;

    @Override
    public void setup() {

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        try {
            sessionCatalogDataList = Arrays.asList(objectMapper.readValue(sessionPath.toFile(), SessionCatalogData[].class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        session = new Session().setRawComplete(false);
        session.setName(sessionName);

        sessionManagementService = new SessionManagementService(sessionService, catalogService, l0uPreparationProperties);
    }

    @Test
    void updateRawComplete() {

        // Given
        Mockito.when(sessionService.exists(sessionName)).thenReturn(true);
        Mockito.when(sessionService.read(sessionName)).thenReturn(session);
        Mockito.when(catalogService.retrieveSessionData(sessionName)).thenReturn(sessionCatalogDataList);
        Mockito.when(sessionService.update(ArgumentMatchers.any())).thenReturn(session);

        // When
        sessionManagementService.updateRawComplete(sessionName);

        // Then
        assertTrue(session.isRawComplete());
        assertEquals(Instant.ofEpochSecond(1658322893), session.getT0PdgsDate());
    }

}
