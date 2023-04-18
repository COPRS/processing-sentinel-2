package eu.csgroup.coprs.ps2.pw.l2.service.prepare;

import eu.csgroup.coprs.ps2.pw.l2.model.L2AuxFile;
import eu.csgroup.coprs.ps2.pw.l2.model.L2Datastrip;
import eu.csgroup.coprs.ps2.pw.l2.model.L2DatastripEntity;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class TestHelper {

    public static final Path FOLDER_PATH = Paths.get("src/test/resources/l1c");
    public static final String DATASTRIP_NAME = "S2B_OPER_MSI_L1C_DS_REFS_20221124T170956_S20221011T064025_N04.00";
    public static final Path DS_PATH = FOLDER_PATH.resolve(DATASTRIP_NAME);
    public static final String FOLDER = FOLDER_PATH.toAbsolutePath().toString();
    public static final Instant START_TIME = Instant.now().minus(60, ChronoUnit.DAYS);
    public static final Instant STOP_TIME = Instant.now().minus(30, ChronoUnit.DAYS);
    public static final Pair<Instant, Instant> DATASTRIP_TIMES = Pair.of(START_TIME, STOP_TIME);
    public static final String SATELLITE = "B";
    public static final Instant T0_PDGS_DATE = Instant.now().minus(2, ChronoUnit.HOURS);

    public static final List<String> TL_LIST = List.of(
            "S2B_OPER_MSI_L1C_TL_REFS_20221124T170956_A029232_T40MDV_N04.00",
            "S2B_OPER_MSI_L1C_TL_REFS_20221124T170956_A029232_T40MCU_N04.00",
            "S2B_OPER_MSI_L1C_TL_REFS_20221124T170956_A029232_T40MDU_N04.00"
    );

    public static final L2Datastrip DATASTRIP = ((L2Datastrip) new L2Datastrip()
            .setFolder(FOLDER)
            .setAvailableByTL(TL_LIST.stream().collect(Collectors.toMap(Function.identity(), o -> false)))
            .setName(DATASTRIP_NAME)
            .setAvailableByAux(Arrays.stream(L2AuxFile.values()).collect(Collectors.toMap(auxFile -> auxFile.getAuxProductType().name(), o -> false)))
            .setStartTime(START_TIME)
            .setStopTime(STOP_TIME)
            .setT0PdgsDate(T0_PDGS_DATE)
            .setSatellite(SATELLITE));

    public static final L2DatastripEntity DATASTRIP_ENTITY = (L2DatastripEntity) new L2DatastripEntity()
            .setFolder(FOLDER)
            .setAvailableByTL(TL_LIST.stream().collect(Collectors.toMap(Function.identity(), o -> false)))
            .setName(DATASTRIP_NAME)
            .setAvailableByAux(Arrays.stream(L2AuxFile.values()).collect(Collectors.toMap(auxFile -> auxFile.getAuxProductType().name(), o -> false)))
            .setStartTime(START_TIME)
            .setStopTime(STOP_TIME)
            .setT0PdgsDate(T0_PDGS_DATE)
            .setSatellite(SATELLITE);

    public static final L2Datastrip UPDATED_DATASTRIP = ((L2Datastrip) new L2Datastrip()
            .setFolder(FOLDER)
            .setAvailableByTL(TL_LIST.stream().collect(Collectors.toMap(Function.identity(), o -> true)))
            .setName(DATASTRIP_NAME)
            .setAvailableByAux(Arrays.stream(L2AuxFile.values()).collect(Collectors.toMap(auxFile -> auxFile.getAuxProductType().name(), o -> true)))
            .setStartTime(START_TIME)
            .setStopTime(STOP_TIME)
            .setT0PdgsDate(T0_PDGS_DATE)
            .setSatellite(SATELLITE)
            .setReady(true));

    private TestHelper() {
    }

}
