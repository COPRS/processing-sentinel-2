package eu.csgroup.coprs.ps2.pw.l0u.service.prepare;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.csgroup.coprs.ps2.core.common.model.catalog.SessionCatalogData;
import eu.csgroup.coprs.ps2.pw.l0u.model.Session;
import eu.csgroup.coprs.ps2.pw.l0u.model.SessionEntity;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public final class TestHelper {

    public static final Path SESSION_PATH = Paths.get("src/test/resources/mdc_session.json");
    public static final int SESSION_FILES_COUNT = 168;
    public static final String SESSION_NAME = "sessionName";
    public static final Instant START_TIME = Instant.now().minus(60, ChronoUnit.DAYS);
    public static final Instant STOP_TIME = Instant.now().minus(30, ChronoUnit.DAYS);
    public static final String SATELLITE = "A";
    public static final String STATION_CODE = "_SGS";
    public static final Instant T0_PDGS_DATE = Instant.now().minus(2, ChronoUnit.HOURS);

    public static final Session SESSION = ((Session) new Session()
            .setName(SESSION_NAME)
            .setStartTime(START_TIME)
            .setStopTime(STOP_TIME)
            .setT0PdgsDate(T0_PDGS_DATE)
            .setSatellite(SATELLITE)
            .setStationCode(STATION_CODE));

    public static final SessionEntity SESSION_ENTITY = new SessionEntity()
            .setName(SESSION_NAME)
            .setStartTime(START_TIME)
            .setStopTime(STOP_TIME)
            .setT0PdgsDate(T0_PDGS_DATE)
            .setSatellite(SATELLITE)
            .setStationCode(STATION_CODE);

    public static final Session UPDATED_SESSION = ((Session) new Session()
            .setRawComplete(true)
            .setName(SESSION_NAME)
            .setStartTime(START_TIME)
            .setStopTime(STOP_TIME)
            .setT0PdgsDate(T0_PDGS_DATE)
            .setSatellite(SATELLITE)
            .setStationCode(STATION_CODE)
            .setReady(true));

    public static final List<SessionCatalogData> SESSION_CATALOG_DATA_LIST;

    static {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        try {
            SESSION_CATALOG_DATA_LIST = Arrays.asList(objectMapper.readValue(TestHelper.SESSION_PATH.toFile(), SessionCatalogData[].class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TestHelper() {
    }

}
